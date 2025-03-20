package com.manager.restaurant.service;

import com.manager.restaurant.component.NotiClient;
import com.manager.restaurant.dto.request.MergeTableRequest;
import com.manager.restaurant.dto.request.OrderRequest;
import com.manager.restaurant.dto.response.Bill.BillResponse;
import com.manager.restaurant.dto.response.Bill.FoodDetails;
import com.manager.restaurant.dto.response.CheckBillResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.OrderResponse;
import com.manager.restaurant.dto.response.UrlResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.*;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class BillService {
    BillRepository billRepository;
    TableRepository tableRepository;
    FoodRepository foodRepository;
    AccountRepository accountRepository;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;

    public CheckBillResponse checkBill(String idTable) {
        RestaurantTable table = tableRepository.findById(idTable).orElseThrow(
                ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        Bill bill;
        if(ObjectUtils.isEmpty(table.getMergedTo())){
            bill = billRepository.findByTable_IdTableAndStatus(idTable, "Open");
        }else {
            bill = billRepository.findByTable_IdTableAndStatus(table.getMergedTo(), "Open");
        }
        return ObjectUtils.isNotEmpty(bill) ?
                new CheckBillResponse(bill.getIdBill()) : new CheckBillResponse(null);
    }

    public BillResponse getBillDetails(String idBill) {
        Bill bill = billRepository.findById(idBill).orElseThrow(
                ()-> new BadException(ErrorCode.BILL_NOT_FOUND)
        );

        BillResponse billResponse = new BillResponse();
        billResponse.setStatus(bill.getStatus());
        billResponse.setIdBill(bill.getIdBill());
        billResponse.setIdTable(bill.getTable().getIdTable());
        billResponse.setNameTable(bill.getTable().getNameTable());
        List<FoodDetails> foodDetails = billRepository.getFoodDetails(bill.getIdBill());
        billResponse.setFoods(foodDetails);
        double total = 0;
        for (FoodDetails foodDetail : foodDetails) {
            total += foodDetail.getPrice() * foodDetail.getQuantity();
        }
        billResponse.setTotal(total);

        return billResponse;
    }


    public OrderResponse orderFood(OrderRequest request) {
        RestaurantTable table = tableRepository.findById(request.getIdTable()).orElseThrow(
                ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        Food food = foodRepository.findById(request.getIdFood()).orElseThrow(
                ()-> new BadException(ErrorCode.FOOD_NOT_FOUND)
        );

        String idFoodRes = foodRepository.getIdRestaurantByIdFood(request.getIdFood());


        //check food and table in 1 res
        String idTableRes = tableRepository.getRestaurantIdByIdTable(request.getIdTable());
        if(ObjectUtils.isEmpty(idFoodRes) || !idTableRes.equals(idFoodRes)) {
            throw new BadException(ErrorCode.RESTAURANT_NOT_MATCH);
        }

        boolean isMerged = false;

        if(ObjectUtils.isNotEmpty(table.getMergedTo())){
            table = tableRepository.findById(table.getMergedTo()).orElseThrow(
                    ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
            isMerged = true;
        }

        //check bill if no ex create new
        Bill bill = billRepository.findByTable_IdTableAndStatus(table.getIdTable(), "Open");
        if(ObjectUtils.isEmpty(bill)){
            bill = Bill.builder()
                    .status("Open")
                    .table(table)
                    .build();
            billRepository.save(bill);
            table.setStatus("Unavailable");
            tableRepository.save(table);
            if(isMerged){
                tableRepository.updateStatus(table.getIdTable(), "Unavailable");
            }
        }

        Order order = Order.builder()
                .bill(bill)
                .quantity(request.getQuantity())
                .food(food)
                .build();
        order = orderRepository.save(order);


        String idRes = table.getRestaurant().getIdRestaurant();
        String tableName = table.getNameTable();
        new Thread(()->{
            notifyClients(idRes, "New order has been placed");
            List<String> deviceTokens = accountRepository.getDeviceTokenByIdRestaurant(idRes);
            if(ObjectUtils.isNotEmpty(deviceTokens)){
                for(String deviceToken : deviceTokens){
                    NotiClient.sendMessgae(deviceToken,"Bàn " + tableName + " có đơn hàng mới");
                }
            }
        }).start();

        return OrderResponse.builder()
                .idOrder(order.getIdOrder())
                .tableName(table.getNameTable())
                .idBill(bill.getIdBill())
                .build();
    }



    private int notifyClients(String idRestaurant, String message) {
        // Notify clients that the bill for the table has been updated
        try {
            int statusCode = sendMessageToSocketServer(idRestaurant, message);
            if (statusCode == HttpStatus.SC_OK) {
                return 1;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                log.error("Client not found");
                return -1;
            } else {
                log.error("Failed to send message to client");
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private String createMethodPayment(double price, String partnerCode, String desUrl, String des, String accessKey,
                                       String secretKey) {
        String payUrl = "";
        String urlString = "http://localhost:3000/api/v1/momo/payment-request";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Cấu hình request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Chuẩn bị dữ liệu JSON
            String json = String.format(
                    "{\"price\":\"%s\", \"partnerCode\":\"%s\", \"desUrl\":\"%s\", \"des\":\"%s\", \"accessKey\":\"%s\", \"secretKey\":\"%s\"}",
                    price, partnerCode, desUrl, des, accessKey, secretKey);

            // Gửi dữ liệu
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi
            int responseCode = connection.getResponseCode();
            log.info("Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    payUrl = response.toString();
                }
            }

            // Đóng kết nối
            connection.disconnect();

        } catch (IOException e) {
            log.error("Error while creating payment method", e);
        }
        return payUrl;
    }


    private int sendMessageToSocketServer(String clientId, String message) throws IOException {
        String url = "http://localhost:3000/api/v1/socket/send-message";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String json = String.format("{\"clientId\":\"%s\", \"message\":\"%s\"}", clientId, message);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = connection.getResponseCode();
        connection.disconnect();

        return statusCode;
    }

    public void cancelOrder(String idOrder) {
        Order order = orderRepository.findById(idOrder).orElseThrow(
                ()-> new BadException(ErrorCode.ORDER_NOT_FOUND)
        );
        orderRepository.delete(order);
    }

    public void updateDoneOrder(String idOrder) {
        Order order = orderRepository.findById(idOrder).orElseThrow(
                ()-> new BadException(ErrorCode.ORDER_NOT_FOUND)
        );
        order.setStatus("Done");
        orderRepository.save(order);
    }

    @Transactional
    public void closeBill(String idTable) {
        RestaurantTable table = tableRepository.findById(idTable).orElseThrow(
                ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        if(ObjectUtils.isNotEmpty(table.getMergedTo())){
            table = tableRepository.findById(table.getMergedTo()).orElseThrow(
                    ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
            List<RestaurantTable> tables = tableRepository.findAllByMergedTo(table.getMergedTo());
            for(RestaurantTable item : tables){
                item.setStatus("Available");
                item.setMergedTo(null);
            }
            tableRepository.saveAll(tables);
        }
        Bill bill = billRepository.findByTable_IdTableAndStatus(table.getIdTable(), "Open");
        if(ObjectUtils.isEmpty(bill)){
            throw new BadException(ErrorCode.BILL_NOT_FOUND);
        }
        bill.setStatus("Closed");
        billRepository.save(bill);

        table.setStatus("Available");
        table.setMergedTo(null);
        tableRepository.save(table);
    }

    @Transactional
    public void mergeTable(MergeTableRequest request) {
        RestaurantTable table1 = tableRepository.findById(request.getIdTable1())
                .orElseThrow(() -> new BadException(ErrorCode.TABLE_NOT_FOUND));
        RestaurantTable table2 = tableRepository.findById(request.getIdTable2())
                .orElseThrow(() -> new BadException(ErrorCode.TABLE_NOT_FOUND));

        if (ObjectUtils.isNotEmpty(table1.getMergedTo())) {
            table2.setMergedTo(table1.getMergedTo());
        } else if (ObjectUtils.isNotEmpty(table2.getMergedTo())) {
            table1.setMergedTo(table2.getMergedTo());
        } else {
            table2.setMergedTo(table1.getIdTable());
        }

        if ("Unavailable".equals(table1.getStatus()) || "Unavailable".equals(table2.getStatus())) {
            table1.setStatus("Unavailable");
            table2.setStatus("Unavailable");
        }

        tableRepository.save(table1);
        tableRepository.save(table2);
    }

    @Transactional
    public void unMergeOneTable(String idTable) {
        RestaurantTable table = tableRepository.findById(idTable).orElseThrow(
                ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        table.setStatus("Available");
        if(ObjectUtils.isNotEmpty(table.getMergedTo())){
            table.setMergedTo(null);
            tableRepository.save(table);
        }else{
            List<RestaurantTable> tables = tableRepository.findAllByMergedTo(table.getIdTable());
            if(ObjectUtils.isNotEmpty(tables)){
                Bill bill = billRepository.findByTable_IdTableAndStatus(table.getIdTable(),"Open");
                if(ObjectUtils.isNotEmpty(bill)){
                    bill.setTable(tables.getFirst());
                    billRepository.save(bill);
                }
                tables.getFirst().setMergedTo(null);
                for(int i=1; i<tables.size(); i++){
                    tables.get(i).setMergedTo(tables.getFirst().getIdTable());
                }
                tableRepository.save(table);
                tableRepository.saveAll(tables);
            }
        }
    }

    public List<BillResponse> getAllFoodOrders(String idRestaurant) {
        List<Bill> bills = billRepository.findByRestaurantIdAndStatus(idRestaurant, "Open");
        List<BillResponse> billResponses = new ArrayList<>();
        for(Bill bill : bills){
            BillResponse billResponse = new BillResponse();
            billResponse.setStatus(bill.getStatus());
            billResponse.setIdBill(bill.getIdBill());
            billResponse.setIdTable(bill.getTable().getIdTable());
            billResponse.setNameTable(bill.getTable().getNameTable());
            List<FoodDetails> foodDetails = billRepository.getFoodDetails(bill.getIdBill());
            billResponse.setFoods(foodDetails);
            double total = 0;
            for (FoodDetails foodDetail : foodDetails) {
                total += foodDetail.getPrice() * foodDetail.getQuantity();
            }
            billResponse.setTotal(total);
            billResponses.add(billResponse);
        }
        return billResponses;
    }


    public BillResponse getAllFoodOrdersForClient(String idTable) {
        RestaurantTable table = tableRepository.findById(idTable).orElseThrow(
                ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        if(ObjectUtils.isNotEmpty(table.getMergedTo())){
            table = tableRepository.findById(table.getMergedTo()).orElseThrow(
                    ()-> new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
        }
        Bill bill = billRepository.findByTable_IdTableAndStatus(idTable, "Open");
        return getBillDetails(bill.getIdBill());
    }

    public UrlResponse paymentAllBill(String idTable) {
        RestaurantTable table = tableRepository.findById(idTable).orElseThrow(
                ()->new BadException(ErrorCode.TABLE_NOT_FOUND)
        );
        if(ObjectUtils.isNotEmpty(table.getMergedTo())){
            table = tableRepository.findById(table.getMergedTo()).orElseThrow(
                    ()->new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
        }
        Payment payment = paymentRepository.findByRestaurant_IdRestaurant(table.getRestaurant().getIdRestaurant()).orElseThrow(
                ()->new BadException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        BillResponse billResponse = getAllFoodOrdersForClient(table.getIdTable());
        String code = billResponse.getIdBill();
        String desUrl = "http://localhost:8082/bills/complete-payment?idTable=" + table.getIdTable() + "&code="
                + code + "&idRes=" + table.getRestaurant().getIdRestaurant();
        String des = "Thanh toan hoa don";
        String payURL = createMethodPayment(billResponse.getTotal(), payment.getPartnerCode(), desUrl, des
                ,payment.getAccessKey(), payment.getSecretKey());
        if(ObjectUtils.isNotEmpty(payURL)){
            return new UrlResponse(payURL);
        }else {
            throw new BadException(ErrorCode.CANT_PAY);
        }
    }
}

package com.manager.restaurant.controller;


import com.manager.restaurant.dto.request.MergeTableRequest;
import com.manager.restaurant.dto.request.OrderRequest;
import com.manager.restaurant.dto.response.Bill.BillResponse;
import com.manager.restaurant.dto.response.CheckBillResponse;
import com.manager.restaurant.dto.response.JsonResponse;
import com.manager.restaurant.dto.response.OrderResponse;
import com.manager.restaurant.dto.response.UrlResponse;
import com.manager.restaurant.service.BillService;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillController {
    BillService billService;

    @GetMapping("/check-bill/{idTable}")
    public JsonResponse<CheckBillResponse> checkBill(@PathVariable("idTable") String idTable) {
        return JsonResponse.success(billService.checkBill(idTable));
    }

    @GetMapping("/get/{idBill}")
    public JsonResponse<BillResponse> getBill(@PathVariable("idBill") String idBill) {
        return JsonResponse.success(billService.getBillDetails(idBill));
    }

    @PostMapping("/order")
    public JsonResponse<OrderResponse> order(@RequestBody OrderRequest request) {
        return JsonResponse.success(billService.orderFood(request));
    }

    @DeleteMapping("/order-cancel/{idOrder}")
    public JsonResponse<String> deleteOrder(@PathVariable("idOrder") String idOrder){
        billService.cancelOrder(idOrder);
        return JsonResponse.success("Cancel success");
    }

    @PutMapping("order-update-complete/{idOrder}")
    public JsonResponse<String> updateOrderDone(@PathVariable("idOrder") String idOrder){
        billService.updateDoneOrder(idOrder);
        return JsonResponse.success("Update success");
    }


    @PutMapping("order-update-received/{idOrder}")
    public JsonResponse<String> updateOrderReceived(@PathVariable("idOrder") String idOrder){
        billService.updateReceivedOrder(idOrder);
        return JsonResponse.success("Update success");
    }

    @PutMapping("close-bill/{idTable}")
    public JsonResponse<String> closeBill(@PathVariable("idTable") String idTable){
        billService.closeBill(idTable);
        return JsonResponse.success("Update success");
    }





    @GetMapping("/get-all-order/{idRestaurant}")
    public JsonResponse<List<BillResponse>> getAllOrder(@PathVariable("idRestaurant") String idRestaurant){
        return JsonResponse.success(billService.getAllFoodOrders(idRestaurant));
    }

    @GetMapping("/get-all-order-client/{idTable}")
    public JsonResponse<BillResponse> getAllOrderClient(@PathVariable("idTable") String idTable){
        return JsonResponse.success(billService.getAllFoodOrdersForClient(idTable));
    }

    @PostMapping("/merge-table")
    public JsonResponse<String> mergeTable(@RequestBody MergeTableRequest request){
        billService.mergeTable(request);
        return JsonResponse.success("Merge table success");
    }

    @PostMapping("/un-merge-table/{idTable}")
    public JsonResponse<String> unMergeTable(@PathVariable("idTable") String idTable){
        billService.unMergeOneTable(idTable);
        return JsonResponse.success("Un merge table success");
    }

    @GetMapping("/payment-all-bill/{idTable}")
    public JsonResponse<UrlResponse> payment(@PathVariable("idTable") String idTable){
        return JsonResponse.success(billService.paymentAllBill(idTable));
    }


}


package com.manager.restaurant.service;

import com.manager.restaurant.dto.response.chart.ChartResponse;
import com.manager.restaurant.dto.response.chart.ChartWeekTableResponse;
import com.manager.restaurant.dto.response.chart.DayOfWeekSales;
import com.manager.restaurant.dto.response.chart.TableChart;
import com.manager.restaurant.helper.DateTimeHelper;
import com.manager.restaurant.repository.BillRepository;
import com.manager.restaurant.repository.TableRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ChartService {
    BillRepository billRepository;
    TableRepository tableRepository;
    public List<ChartResponse> getMonthlyChart(String idRestaurant, int month, int year) {
        var queryRes = billRepository.getMonthlyChart(month,year,idRestaurant).orElseThrow();
        List<ChartResponse> charts = new ArrayList<>();
        for(Object[] item : queryRes){
            charts.add(ChartResponse.toChartResponse(item));
        }
        return charts;
    }

    public List<ChartWeekTableResponse> getWeekChartForTable(String idRestaurant) {
        String lastWeekStart = DateTimeHelper.getSundayOfTwoWeekAgo();
        String lastWeekEnd = DateTimeHelper.getSundayOfLastWeek();
        String thisWeekStart = DateTimeHelper.getSundayOfLastWeek();
        String thisWeekEnd = DateTimeHelper.getSundayOfWeek();
        List<ChartWeekTableResponse> charts = new ArrayList<>();
        for(var item : tableRepository.getTableCharts(lastWeekStart,lastWeekEnd,idRestaurant).orElseThrow()){
            charts.add(new ChartWeekTableResponse((String) item[0],(String) item[1]));
        }
        for(var item : charts) {
            item.setChartListLastWeek(DayOfWeekSales.toDayOfWeekSales(billRepository.getDateOfWeekSale(lastWeekStart, lastWeekEnd, item.getIdTable()).orElseThrow()));
            item.setChartListThisWeek(DayOfWeekSales.toDayOfWeekSales(billRepository.getDateOfWeekSale(thisWeekStart, thisWeekEnd, item.getIdTable()).orElseThrow()));
        }
        return charts;
    }
}

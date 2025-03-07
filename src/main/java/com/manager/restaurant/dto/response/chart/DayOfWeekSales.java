package com.manager.restaurant.dto.response.chart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayOfWeekSales {
    private int dayOfWeek;
    private double totalSales;

    public static DayOfWeekSales toDayOfWeekSale(Object[] objects) {
        return new DayOfWeekSales((int) objects[0], (double) objects[1]);
    }
    public static List<DayOfWeekSales> toDayOfWeekSales(List<Object[]> objects) {
        List<DayOfWeekSales> res = new ArrayList<>();
        for(var item: objects){
            res.add(DayOfWeekSales.toDayOfWeekSale(item));
        }
        return res;
    }
}

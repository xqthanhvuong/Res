package com.manager.restaurant.dto.response.chart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponse {
    private int day;
    private double totalPrice;

    public static ChartResponse toChartResponse(Object[] objects){
        return new ChartResponse((int) objects[0],(double) objects[1]);
    }
}

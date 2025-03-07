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
public class ChartWeekTableResponse {
    private String idTable;
    private String tableName;
    private List<DayOfWeekSales> chartListLastWeek;
    private List<DayOfWeekSales> chartListThisWeek;

    public ChartWeekTableResponse(String idTable, String tableName) {
        this.idTable = idTable;
        this.tableName = tableName;
        this.chartListLastWeek = new ArrayList<>();
        this.chartListThisWeek = new ArrayList<>();
    }
}

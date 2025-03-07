package com.manager.restaurant.dto.response.chart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableChart {
    private String id_table;
    private String name_table;

    public static TableChart toTableChart (Object[] objects){
        return new TableChart((String) objects[0], (String) objects[1]);
    }
}

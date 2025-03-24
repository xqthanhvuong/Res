package com.manager.restaurant.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {
    String idTable;
    String nameTable;
    String mergedTo;
    String status;
}

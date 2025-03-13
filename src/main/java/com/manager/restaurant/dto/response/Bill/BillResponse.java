package com.manager.restaurant.dto.response.Bill;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillResponse {
    String idTable;
    List<FoodDetails> foods;
    double total;
    String status;
}

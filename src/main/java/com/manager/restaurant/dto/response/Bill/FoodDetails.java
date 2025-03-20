package com.manager.restaurant.dto.response.Bill;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodDetails {
    String idFood;
    String name;
    double price;
    String image;
    int quantity;
    String idOrder;
    String paid;
}

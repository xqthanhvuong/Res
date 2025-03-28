package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest {
    String idFood;
    String idMenu;
    String name;
    double price = 0.0;
    String image;
    String type;
}

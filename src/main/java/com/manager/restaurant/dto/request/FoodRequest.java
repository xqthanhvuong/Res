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
    private String idFood;
    private String idMenu;
    private String name;
    private double price = 0.0;
    private String image;
}

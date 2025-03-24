package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {
    private String idMenu;
    private String idRestaurant;
    private String name;
    private String status;
}

package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {
    String idAccount;
    String name;
    String address;
}

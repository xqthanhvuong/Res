package com.manager.restaurant.dto.request;

import com.manager.restaurant.entity.AccountRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffRequest {
    String username;
    String password;
    String name;
    String phone;
    AccountRole role;
    String idRestaurant;
    int salary;
    String type;
    String bankNumber;
    String bankName;
}

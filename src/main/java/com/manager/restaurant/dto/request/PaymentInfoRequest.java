package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoRequest {
    String username;
    int salary;
    String bank;
    String bankAccountNumber;
    String type;
}

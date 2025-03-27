package com.manager.restaurant.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPaymentResponse {
    String userId;
    String name;
    String role;
    float shifts;
    float baseSalary;
    String username;
    int payment;
    String type;
    String bankAccountNumber;
    String bank;
    String workStartDate;
}

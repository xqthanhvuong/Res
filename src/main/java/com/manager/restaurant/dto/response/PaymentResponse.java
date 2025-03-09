package com.manager.restaurant.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    String idPayment;

    String idRestaurant;

    String partnerCode;

    String accessKey;

    String secretKey;

}

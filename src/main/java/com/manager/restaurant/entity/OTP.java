package com.manager.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "otps")
public class OTP {

    @Id
    @Column(name = "id_otp")
    String otpId;

    @Column(name = "phone")
    String phone;

    @Column(name = "otp_code")
    String otpCode;

    @Column(name = "used")
    boolean used;

     @PrePersist
     protected void onCreate() {
         if (ObjectUtils.isEmpty(otpId)) {
             otpId = UUID.randomUUID().toString();
         }
     }
}

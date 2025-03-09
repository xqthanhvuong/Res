package com.manager.restaurant.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;

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
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "id_payment")
    String idPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant", nullable = false)
    Restaurant restaurant;

    @Column
    String partnerCode;

    @Column
    String accessKey;

    @Column
    String secretKey;


    @PrePersist
    protected void onCreate() {
        if(ObjectUtils.isEmpty(idPayment)){
            idPayment = UUID.randomUUID().toString();
        }
    }

}


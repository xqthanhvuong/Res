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
@Table(name = "staff_payments")
public class StaffPayment {
    @Id
    @Column(name = "id_staff_payment")
    String idStaffPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    Account account;

    @Column(name = "salary", nullable = false)
    int salary;

    @Column(name = "type", nullable = false)
    String type;

    @Column(name = "bank_account_number", nullable = false)
    String bankAccountNumber;

    @Column(name = "bank", nullable = false)
    String bank;

    @PrePersist
    protected void onCreate() {
        if(ObjectUtils.isEmpty(idStaffPayment)){
            idStaffPayment = UUID.randomUUID().toString();
        }
    }
}

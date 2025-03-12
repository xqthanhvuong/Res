package com.manager.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "workdays")
public class WorkDay {
    @Id
    @Column(name = "id_workday")
    String idWorkDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    Account account;

    @Column(name = "work_date", nullable = true)
    Timestamp workDate;

    @Column(name = "start_time", nullable = true)
    Time startTime;

    @Column(name = "end_time", nullable = true)
    Time endTime;

    @PrePersist
    protected void onCreate() {
        if(ObjectUtils.isEmpty(idWorkDay)){
            idWorkDay = UUID.randomUUID().toString();
        }
    }
}

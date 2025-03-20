package com.manager.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@jakarta.persistence.Table (name = "tables")
public class RestaurantTable {

    @Id
    @Column(name = "id_table")
    String idTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurant", nullable = false)
    Restaurant restaurant;

    @Column(name = "name_table", nullable = false)
    String nameTable;

    @Column(nullable = false)
    String status;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @Column(name = "code")
    String code;

    @Column(name = "merged_to")
    String mergedTo;

    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
        if(ObjectUtils.isEmpty(idTable)){
            idTable = UUID.randomUUID().toString();
        }
        if(ObjectUtils.isEmpty(status)){
            status = "Available";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}


package com.manager.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Set;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "reports")
public class Report {
    @Id
    @Column(name = "id_report")
    String idReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_workday", nullable = false)
    WorkDay workDay;

    @Column(name = "note")
    String note;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    Set<ReportImage> reportImages;

    @PrePersist
    protected void onCreate() {
        if(ObjectUtils.isEmpty(idReport)){
            idReport = UUID.randomUUID().toString();
        }
    }
}

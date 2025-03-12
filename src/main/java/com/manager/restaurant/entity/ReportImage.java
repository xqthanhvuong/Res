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
@Table(name = "report_images")
public class ReportImage {
    @Id
    @Column(name = "id_report_images")
    String idReportImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_report", nullable = false)
    Report report;

    @Column(name = "url", nullable = false)
    String url;

    @PrePersist
    protected void onCreate() {
        if(ObjectUtils.isEmpty(idReportImage)){
            idReportImage = UUID.randomUUID().toString();
        }
    }
}

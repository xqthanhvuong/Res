package com.manager.restaurant.dto.response;

import com.manager.restaurant.entity.ReportImage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedReportResponse {
    String idReport;

    String note;

    String employeeName;

    Timestamp startDate;

    Set<ReportImageResponse> reportImages;

    public AdvancedReportResponse(String idReport, String note, String employeeName, Timestamp startDate){
        this.idReport = idReport;
        this.note = note;
        this.employeeName = employeeName;
        this.startDate = startDate;
    }
}

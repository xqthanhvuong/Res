package com.manager.restaurant.dto.response;

import com.manager.restaurant.entity.ReportImage;
import com.manager.restaurant.entity.WorkDay;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    String idReport;

    String note;

    Set<ReportImageResponse> reportImages;


}

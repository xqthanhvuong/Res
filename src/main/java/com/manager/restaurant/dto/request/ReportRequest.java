package com.manager.restaurant.dto.request;

import com.manager.restaurant.dto.response.ReportImageResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    String idReport;

    String idWorkDay;

    String note;

    List<String> imageUrls;
}

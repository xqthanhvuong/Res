package com.manager.restaurant.dto.response;

import com.manager.restaurant.entity.ReportImage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportImageResponse {
    String idReportImage;
    String url;

    public static Set<ReportImageResponse> toSet(Set<ReportImage> reportImages){
        Set<ReportImageResponse> responses = new HashSet<>();
        for(var item : reportImages){
            responses.add(new ReportImageResponse(item.getIdReportImage(), item.getUrl()));
        }
        return responses;
    }
}

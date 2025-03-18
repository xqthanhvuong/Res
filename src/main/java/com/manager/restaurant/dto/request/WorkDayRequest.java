package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayRequest {
    String idWorkDay;

    String username;
    // ISO
    String workDate;
    // HH:MM:SS
    String startTime;
    // HH:MM:SS
    String endTime;
}

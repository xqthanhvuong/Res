package com.manager.restaurant.dto.response;

import com.manager.restaurant.entity.WorkDay;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkDayResponse {
    String idWorkDay;
    Timestamp workDate;
    Time startTime;
    Time endTime;

    public WorkDayResponse(WorkDay workDay) {
        idWorkDay = workDay.getIdWorkDay();
        workDate = workDay.getWorkDate();
        startTime = workDay.getStartTime();
        endTime = workDay.getEndTime();
    }
    public static List<WorkDayResponse> ToList(List<WorkDay> workDays) {
        List<WorkDayResponse> res = new ArrayList<>();
        for(var workDay : workDays) {
            res.add(new WorkDayResponse(workDay));
        }
        return res;
    }
}

package com.manager.restaurant.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkDayRequest {

    /**
     * Staff username
     */
    String username;
    /**
     * For fun time staff: date time ISO string
     */
    List<String> dayOffs;

    /**
     * For part-time staff: date time ISO string
     */
    List<String> workDays;

    /**
     *  For hour staff
     *  date time ISO string
     */
    List<String> timeStarts;

    /**
     * For hour staff
     * date time ISO string
     */
    List<String> timeEnds;
}

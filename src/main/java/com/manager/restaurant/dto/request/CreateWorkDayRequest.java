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
     * YYYY-MM-DD
     */
    List<String> dayOffs;

    /**
     * For part-time staff: date time ISO string
     * YYYY-MM-DD
     */
    List<String> workDays;

    /**
     *  For hour staff
     *  time ISO string: HH:MM:SS
     */
    List<String> timeStarts;

    /**
     * For hour staff
     * time ISO string HH:MM:SS
     */
    List<String> timeEnds;
}

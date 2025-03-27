package com.manager.restaurant.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    String idMenu;
    Timestamp createAt;
    String name;
    String status;
}

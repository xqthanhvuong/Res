package com.manager.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// @Table(name = "otps")
public class MessageSocket {

    String clientID;

    String message;

}

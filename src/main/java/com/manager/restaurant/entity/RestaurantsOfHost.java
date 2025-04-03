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
@Table(name = "restaurants_of_host")
@IdClass(RestaurantsOfHostPK.class)
public class RestaurantsOfHost {
    @Id
    @Column(name = "id_restaurant")
    String idRestaurant;

    @Id
    @Column(name = "id_account")
    String idAccount;

}

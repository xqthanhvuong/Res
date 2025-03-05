package com.manager.restaurant.mapper;

import com.manager.restaurant.dto.request.RestaurantRequest;
import com.manager.restaurant.dto.response.RestaurantResponse;
import com.manager.restaurant.entity.Restaurant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    Restaurant toRestaurant(RestaurantRequest request);

    RestaurantResponse toRestaurantResponse(Restaurant restaurant);
}

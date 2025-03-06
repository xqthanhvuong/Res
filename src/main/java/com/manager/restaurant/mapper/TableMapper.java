package com.manager.restaurant.mapper;

import com.manager.restaurant.dto.response.TableResponse;
import com.manager.restaurant.entity.RestaurantTable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {
    TableResponse toTableResponse(RestaurantTable table);
}

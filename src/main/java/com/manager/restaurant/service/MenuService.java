package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.MenuRequest;
import com.manager.restaurant.dto.response.MenuResponse;
import com.manager.restaurant.entity.Menu;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.MenuRepository;
import com.manager.restaurant.repository.RestaurantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class MenuService {
    OwnerCheckingService ownerCheckingService;
    RestaurantRepository restaurantRepository;
    MenuRepository menuRepository;

    public String addMenu(MenuRequest request) {
        // Check for user role before add
        Menu menu = Menu.builder()
                .idMenu(UUID.randomUUID().toString())
                .restaurant(restaurantRepository.getReferenceById(request.getIdRestaurant()))
                .name(request.getName())
                .build();
        if(!ownerCheckingService.isOwner(menu))
            throw new BadException(ErrorCode.USER_IS_BLOCK);
        menuRepository.save(menu);
        return "success";
    }

    public List<MenuResponse> getMenu(String idRestaurant) {
        return menuRepository.getMenuByRestaurantId(idRestaurant).orElseThrow(() -> new BadException(ErrorCode.NOT_FOND));
    }

    public String deleteMenu(String idMenu) {
        Menu menu = menuRepository.getReferenceById(idMenu);
        if(menu.getIdMenu() == null) throw new BadException(ErrorCode.INVALID_KEY);
        if(!ownerCheckingService.isOwner(menu)) throw new BadException(ErrorCode.ACCESS_DENIED);
        menu.setStatus("Inactive");
        menuRepository.save(menu);
        return "OK";
    }

    public String updateMenu(MenuRequest request) {
        Menu menu = menuRepository.getReferenceById(request.getIdMenu());
        if(menu.getIdMenu() == null) throw new BadException(ErrorCode.INVALID_KEY);
        if(!ownerCheckingService.isOwner(menu)) throw new BadException(ErrorCode.ACCESS_DENIED);
        menu.setName(request.getName());
        menuRepository.save(menu);
        return "OK";
    }
}

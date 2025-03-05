package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.RestaurantRequest;
import com.manager.restaurant.dto.request.UpdateRestaurantRequest;
import com.manager.restaurant.dto.response.RestaurantResponse;
import com.manager.restaurant.entity.Account;
import com.manager.restaurant.entity.AccountRole;
import com.manager.restaurant.entity.Restaurant;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.RestaurantMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantRepository;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class RestaurantService {
    RestaurantRepository restaurantRepository;
    AccountRepository accountRepository;
    RestaurantMapper restaurantMapper;

    public void createRestaurant(RestaurantRequest request) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        Restaurant restaurant = restaurantMapper.toRestaurant(request);
        restaurant.setStatus("active");
        restaurantRepository.save(restaurant);
        account.setRestaurant(restaurant);
        accountRepository.save(account);
    }


    public void updateNameRestaurant(UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getIdRestaurant()).orElseThrow(
                ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOND)
        );
        restaurant.setName(request.getName());
        restaurantRepository.save(restaurant);
    }

    public void delete(String idRestaurant) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(AccountRole.Owner.toString().equals(account.getRole())){
            Restaurant restaurant = restaurantRepository.findById(idRestaurant).orElseThrow(
                    ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOND)
            );
            List<Account> accounts = accountRepository.findAllByRestaurant_IdRestaurant(idRestaurant);
            accountRepository.deleteAll(accounts);
            restaurantRepository.delete(restaurant);
        }else {
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
    }

    public RestaurantResponse findById(String idRestaurant) {
        return restaurantMapper.toRestaurantResponse(restaurantRepository.findById(idRestaurant).orElseThrow(
                ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOND)
        ));
    }
}

package com.manager.restaurant.service;

import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantsOfHostRepository;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class OwnerCheckingService {
    AccountRepository accountRepository;
    RestaurantsOfHostRepository restaurantsOfHostRepository;

    public boolean isOwner(Menu menu){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Owner.toString()) &&
                restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(menu.getRestaurant().getIdRestaurant(),account.getIdAccount()));
    }

    public boolean isTableOwner(RestaurantTable table){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Owner.toString()) &&
                restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(table.getRestaurant().getIdRestaurant(),account.getIdAccount()));
    }

    public boolean iStaffOwner(Account staff){
        String idRestaurant = staff.getRestaurant().getIdRestaurant();
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Owner.toString()) &&
                restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(idRestaurant,account.getIdAccount()));
    }
}

package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.RestaurantRequest;
import com.manager.restaurant.dto.request.UpdateRestaurantRequest;
import com.manager.restaurant.dto.response.RestaurantResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.RestaurantMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantRepository;
import com.manager.restaurant.repository.RestaurantsOfHostRepository;
import com.manager.restaurant.repository.TableRepository;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class RestaurantService {
    RestaurantRepository restaurantRepository;
    AccountRepository accountRepository;
    RestaurantMapper restaurantMapper;
    RestaurantsOfHostRepository restaurantsOfHostRepository;
    TableRepository tableRepository;

    public void createRestaurant(RestaurantRequest request) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        Restaurant restaurant = restaurantMapper.toRestaurant(request);
        restaurant.setStatus("active");
        restaurantRepository.save(restaurant);
        restaurantsOfHostRepository.save(new RestaurantsOfHost(restaurant.getIdRestaurant(), account.getIdAccount()));
    }


    public void updateRestaurant(UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getIdRestaurant()).orElseThrow(
                ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
        );
        restaurant.setName(request.getName());
        restaurant.setAddress(restaurant.getAddress());
        if(!request.getStatus().equals("Active")){
            restaurant.setStatus("Inactive");
            List<Account> accounts = accountRepository.findAllByRestaurant_IdRestaurant(restaurant.getIdRestaurant());
            accounts.removeIf(acc->acc.getRole().equals(AccountRole.Owner.toString()));
            for (Account ac: accounts) {
                ac.setStatus("Inactive");
            }
            accountRepository.saveAll(accounts);
        }
        restaurantRepository.save(restaurant);
    }

    public void delete(String idRestaurant) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(AccountRole.Owner.toString().equals(account.getRole())){
            Restaurant restaurant = restaurantRepository.findById(idRestaurant).orElseThrow(
                    ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
            );
            if(tableRepository.existsByRestaurantAndStatus(restaurant,"Unavailable")){
                throw new BadException(ErrorCode.CANT_DELETE_RES);
            }

            RestaurantsOfHostPK restaurantsOfHostPK = new RestaurantsOfHostPK(account.getIdAccount(), restaurant.getIdRestaurant());
            if(!restaurantsOfHostRepository.existsById(restaurantsOfHostPK)){
                throw new BadException(ErrorCode.ACCESS_DENIED);
            }
            restaurantsOfHostRepository.deleteById(restaurantsOfHostPK);
            List<Account> accounts = accountRepository.findAllByRestaurant_IdRestaurant(idRestaurant);
            accounts.removeIf(acc->acc.getRole().equals(AccountRole.Owner.toString()));
            for (Account ac: accounts) {
                ac.setStatus("Inactive");
            }
            accountRepository.saveAll(accounts);
            restaurantRepository.delete(restaurant);
            account.setRestaurant(null);
            accountRepository.save(account);
        }else {
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
    }

    public RestaurantResponse findById(String idRestaurant) {
        return restaurantMapper.toRestaurantResponse(restaurantRepository.findById(idRestaurant).orElseThrow(
                ()-> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
        ));
    }

    public List<RestaurantResponse> getMyRestaurant() {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        List<RestaurantResponse> rp = new ArrayList<>();
        if(AccountRole.Owner.toString().equals(account.getRole())){
            List<RestaurantsOfHost> restaurantsOfHosts = restaurantsOfHostRepository.findByIdAccount(account.getIdAccount());
            for (RestaurantsOfHost resOfHost: restaurantsOfHosts) {
                rp.add(restaurantMapper.toRestaurantResponse(restaurantRepository.findById(resOfHost.getIdRestaurant()).orElseThrow()));
            }
        }else {
            rp.add(restaurantMapper.toRestaurantResponse(account.getRestaurant()));
        }
        return rp;
    }
}

package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.TableRequest;
import com.manager.restaurant.dto.response.TableResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.TableMapper;
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
public class TableService {
    TableRepository tableRepository;
    RestaurantRepository restaurantRepository;
    AccountRepository accountRepository;
    TableMapper tableMapper;
    RestaurantsOfHostRepository restaurantsOfHostRepository;

    public void createTable(TableRequest request) {
        executeIfOwner(request, req -> {
            Restaurant restaurant = restaurantRepository.findById(req.getIdRestaurant()).orElseThrow(
                    () -> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
            );
            Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                    ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
            );
            if(restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(restaurant.getIdRestaurant(),account.getIdAccount()))
                    || account.getRestaurant().getIdRestaurant().equals(restaurant.getIdRestaurant())){
                RestaurantTable table = new RestaurantTable();
                table.setNameTable(request.getNameTable());
                table.setRestaurant(restaurant);
                tableRepository.save(table);
            }else {
                throw new BadException(ErrorCode.ACCESS_DENIED);
            }
        });
    }


    public void deleteTable(String idTable) {
        executeIfOwner(idTable, id -> {
            RestaurantTable table = tableRepository.findById(id).orElseThrow(
                    () -> new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
            Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                    ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
            );
            if(restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(table.getRestaurant().getIdRestaurant(),account.getIdAccount()))
                    || account.getRestaurant().getIdRestaurant().equals(table.getRestaurant().getIdRestaurant())){
                tableRepository.delete(table);
            }else {
                throw new BadException(ErrorCode.ACCESS_DENIED);
            }
        });
    }

    public List<TableResponse> getAllTableOfRestaurant(String idRestaurant) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        Restaurant restaurant = restaurantRepository.findById(idRestaurant).orElseThrow(
                () -> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
        );
        if(restaurantsOfHostRepository.existsById(new RestaurantsOfHostPK(idRestaurant, account.getIdAccount()))
                || account.getRestaurant().getIdRestaurant().equals(idRestaurant)) {
            List<RestaurantTable> tables = tableRepository.findByRestaurant_IdRestaurant(idRestaurant);
            List<TableResponse> rs = new ArrayList<>();
            for(RestaurantTable table : tables) {
                rs.add(tableMapper.toTableResponse(table));
            }
            return rs;
        }else {
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
    }


    @FunctionalInterface
    public interface OwnerAction<T> {
        void perform(T request);
    }

    public <T> void executeIfOwner(T request, OwnerAction<T> action) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        if (account.getRole().equals(AccountRole.Owner.toString())) {
            action.perform(request);
        } else {
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
    }
}

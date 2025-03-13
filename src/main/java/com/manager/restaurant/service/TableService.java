package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.TableRequest;
import com.manager.restaurant.dto.response.TableResponse;
import com.manager.restaurant.entity.Account;
import com.manager.restaurant.entity.AccountRole;
import com.manager.restaurant.entity.Restaurant;
import com.manager.restaurant.entity.RestaurantTable;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.TableMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantRepository;
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

    public void createTable(TableRequest request) {
        executeIfOwner(request, req -> {
            Restaurant restaurant = restaurantRepository.findById(req.getIdRestaurant()).orElseThrow(
                    () -> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
            );
            RestaurantTable table = new RestaurantTable();
            table.setRestaurant(restaurant);
            tableRepository.save(table);
        });
    }


    public void deleteTable(String idTable) {
        executeIfOwner(idTable, id -> {
            RestaurantTable table = tableRepository.findById(id).orElseThrow(
                    () -> new BadException(ErrorCode.TABLE_NOT_FOUND)
            );
            tableRepository.delete(table);
        });
    }

    public List<TableResponse> getAllTableOfRestaurant(String idRestaurant) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(account.getRestaurant().getIdRestaurant().equals(idRestaurant)) {
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

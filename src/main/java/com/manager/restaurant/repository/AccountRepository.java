package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByUsernameAndStatus(String username, String status);


    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Optional<Account> findByIdAccount(String idAccount);

    List<Account> findAllByIdAccountIsIn(List<String> accountId);

    List<Account> findAllByRestaurant_IdRestaurant(String idRestaurant);

    Optional<String> findPhoneByUsername(String username);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Account a SET a. = true WHERE a.phone = :phone")
//    int activateAccountByPhone(String phone);

    @Query("select a.deviceToken from Account a where a.restaurant.idRestaurant = :idRes")
    List<String> getDeviceTokenByIdRestaurant(@Param("idRes") String idRestaurant);
    @Query("SELECT a.username FROM Account a WHERE a.restaurant.idRestaurant = :idRestaurant AND a.status = :status AND a.role <> 'Owner'")
    Optional<List<String>> findUsernamesByRestaurantAndStatus(@Param("idRestaurant") String idRestaurant, @Param("status") String status);

    Optional<Account> findByPhone(String phone);
}

package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Account;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    Optional<Account> findByIdAccount(String idAccount);

    List<Account> findAllByRestaurant_IdRestaurant(String idRestaurant);
}

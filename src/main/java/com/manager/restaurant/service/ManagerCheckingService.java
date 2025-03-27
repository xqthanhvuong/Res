package com.manager.restaurant.service;

import com.manager.restaurant.entity.Account;
import com.manager.restaurant.entity.AccountRole;
import com.manager.restaurant.entity.Menu;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.AccountRepository;
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
public class ManagerCheckingService {
    AccountRepository accountRepository;

    public boolean isManagerOrOwner(){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        return account.getRole().equals(AccountRole.Manager.name()) || account.getRole().equals(AccountRole.Owner.name());
    }
}

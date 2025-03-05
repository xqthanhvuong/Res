package com.manager.restaurant.mapper;

import com.manager.restaurant.dto.request.AccountRequest;
import com.manager.restaurant.dto.request.StaffRequest;
import com.manager.restaurant.dto.response.AccountResponse;
import com.manager.restaurant.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(AccountRequest accountRequest);
    Account staffToAccount(StaffRequest accountRequest);
    AccountResponse toAccountResponse(Account account);
}

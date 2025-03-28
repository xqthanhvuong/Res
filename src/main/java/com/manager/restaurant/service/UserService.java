package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.*;
import com.manager.restaurant.dto.response.AccountResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.AccountMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantRepository;
import com.manager.restaurant.repository.StaffPaymentRepository;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class UserService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    RestaurantRepository restaurantRepository;
    StaffPaymentRepository staffPaymentRepository;
    ManagerCheckingService managerCheckingService;


    public void createAccount(AccountRequest accountRequest) {
        if(accountRepository.existsByUsername(accountRequest.getUsername())){
            throw new BadException(ErrorCode.USER_EXISTED);
        }
        if(accountRepository.existsByPhone(accountRequest.getPhone())){
            throw new BadException(ErrorCode.PHONE_EXISTED);
        }
        Account account = accountMapper.toAccount(accountRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setStatus(AccountStatus.Active.toString());
        account.setRole(AccountRole.Owner.name());
        accountRepository.save(account);
    }

    public void updateAccount(AccountUpdateRequest accountRequest) {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        account.setName(accountRequest.getName());
        account.setAvt(accountRequest.getAvt());
        account.setBirthdate(accountRequest.getBirthDate());
        accountRepository.save(account);
    }

    public void createStaff(StaffRequest staffRequest) {
        Restaurant restaurant = restaurantRepository.findById(staffRequest.getIdRestaurant()).orElseThrow(
                () -> new BadException(ErrorCode.RESTAURANT_NOT_FOUND)
        );
        Account account = accountMapper.staffToAccount(staffRequest);
        account.setRestaurant(restaurant);
        account.setStatus(AccountStatus.Active.toString());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        StaffPayment staffPayment = StaffPayment.builder()
                .salary(staffRequest.getSalary())
                .type(staffRequest.getType())
                .bankAccountNumber(staffRequest.getBankNumber())
                .bank(staffRequest.getBankName())
                .account(account)
                .build();
        accountRepository.save(account);
        staffPaymentRepository.save(staffPayment);
    }

    public void updateDeviceToken(UpdateDeviceRequest request){
        Account account = accountRepository.findByIdAccount(request.getIdAccount()).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        account.setDeviceToken(request.getDeviceToken());
        accountRepository.save(account);
    }

    public AccountResponse getMyInfo() {
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        AccountResponse accountResponse =  accountMapper.toAccountResponse(account);
        if(ObjectUtils.isNotEmpty(account.getRestaurant())){
            accountResponse.setIdRes(account.getRestaurant().getIdRestaurant());
        }
        StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(account.getUsername()).orElseThrow();
        if(ObjectUtils.isNotEmpty(staffPayment)){
            accountResponse.setBankName(staffPayment.getBank());
            accountResponse.setBankNumber(staffPayment.getBankAccountNumber());
            accountResponse.setSalary(staffPayment.getSalary());
            accountResponse.setPaymentType(staffPayment.getType());
        }
        return  accountResponse;
    }

    public AccountResponse getInfo(String userName) {
        Account account = accountRepository.findByUsername(userName).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        AccountResponse accountResponse =  accountMapper.toAccountResponse(account);
        StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(userName).orElseThrow();
        if(ObjectUtils.isNotEmpty(staffPayment)){
            accountResponse.setBankName(staffPayment.getBank());
            accountResponse.setBankNumber(staffPayment.getBankAccountNumber());
            accountResponse.setSalary(staffPayment.getSalary());
            accountResponse.setPaymentType(staffPayment.getType());
        }
        return accountResponse;
    }

    public void deleteAccount(String idAccount) {
        if(!managerCheckingService.isManagerOrOwner()){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        Account account = accountRepository.findByIdAccount(idAccount).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        account.setStatus("Inactive");
        accountRepository.save(account);
    }

    public void updateStaff(String idAccount, UpdateStaffRequest request) {
        if(!managerCheckingService.isManagerOrOwner()){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        Account account = accountRepository.findById(idAccount).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        account.setPhone(request.getPhone());
        account.setName(request.getName());
        account.setRole(request.getRole().name());
        StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(account.getUsername()).orElseThrow();
        if(ObjectUtils.isNotEmpty(staffPayment)){
            staffPayment.setSalary(request.getSalary());
            staffPayment.setType(request.getType());
            staffPayment.setBankAccountNumber(request.getBankNumber());
            staffPayment.setBank(request.getBankName());
            staffPaymentRepository.save(staffPayment);
        }
        accountRepository.save(account);

    }
}

package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.*;
import com.manager.restaurant.dto.response.AccountResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.mapper.AccountMapper;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.RestaurantRepository;
import com.manager.restaurant.repository.RestaurantsOfHostRepository;
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

import java.util.HashSet;
import java.util.List;

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
    RestaurantsOfHostRepository restaurantsOfHostRepository;


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
        if(staffRequest.getRole() == AccountRole.Owner){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
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
        try {
            StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(account.getUsername()).orElseThrow();
            accountResponse.setBankName(staffPayment.getBank());
            accountResponse.setBankNumber(staffPayment.getBankAccountNumber());
            accountResponse.setSalary(staffPayment.getSalary());
            accountResponse.setPaymentType(staffPayment.getType());
        } catch(Exception e) { }
        return  accountResponse;
    }

    public AccountResponse getInfo(String userName) {
        Account account = accountRepository.findByUsername(userName).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        AccountResponse accountResponse =  accountMapper.toAccountResponse(account);
        try {
            StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(userName).orElseThrow();
            accountResponse.setBankName(staffPayment.getBank());
            accountResponse.setBankNumber(staffPayment.getBankAccountNumber());
            accountResponse.setSalary(staffPayment.getSalary());
            accountResponse.setPaymentType(staffPayment.getType());
        } catch (Exception e) { }
        return accountResponse;
    }

    // TODO: check user restaurant
    public void deleteAccount(String idAccount) {
        if(!managerCheckingService.isManagerOrOwner()){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        Account ownerAccount = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        Account account = accountRepository.findByIdAccount(idAccount).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        int records = restaurantsOfHostRepository.countAllByIdAccountAndIdRestaurant(ownerAccount.getIdAccount(), account.getRestaurant().getIdRestaurant());
        if(records == 0){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
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
        try {
            StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(account.getUsername()).orElseThrow();
            staffPayment.setSalary(request.getSalary());
            staffPayment.setType(request.getType());
            staffPayment.setBankAccountNumber(request.getBankNumber());
            staffPayment.setBank(request.getBankName());
            staffPaymentRepository.save(staffPayment);
        } catch (Exception e) { }
        accountRepository.save(account);

    }

    public String deleteMany(AccountIdRequest request) {
        if(!managerCheckingService.isManagerOrOwner()){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        Account ownerAccount = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        List<Account> accounts = accountRepository.findAllByIdAccountIsIn(request.getAccountIds());
        // get resId list
        HashSet<String> resIds = new HashSet<>(accounts.stream().map((account) -> account.getRestaurant().getIdRestaurant()).toList());
        int records = restaurantsOfHostRepository.countAllByIdAccountAndIdRestaurantIsIn(ownerAccount.getIdAccount(), resIds);
        if(records != resIds.size()){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        for(var account : accounts) {
            account.setStatus("Inactive");
        }
        accountRepository.saveAll(accounts);
        return "OK";
    }
}

package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.PaymentInfoRequest;
import com.manager.restaurant.dto.response.StaffPaymentResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.StaffPaymentRepository;
import com.manager.restaurant.repository.WorkDayRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class StaffPaymentService {
    StaffPaymentRepository staffPaymentRepository;
    WorkDayRepository workDayRepository;
    AccountRepository accountRepository;
    OwnerCheckingService ownerCheckingService;

    public StaffPaymentResponse getStaffSalary(String staffUsername, int month, int year) {
        // count work day in month year
        StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(staffUsername)
                .orElse(null);

        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);
        Timestamp start = Timestamp.valueOf(startOfMonth);
        Timestamp end = Timestamp.valueOf(endOfMonth);

        // check payment type to calculate
        StaffPaymentResponse response = null;
        if(staffPayment == null){
            response = getStaffInfo(staffUsername);
        } else if(staffPayment.getType().equals(PaymentType.FullTime.name())){
            response = getFullTimeStaffPayment(staffUsername, start, end, staffPayment);
        } else if(staffPayment.getType().equals(PaymentType.PartTime.name())){
            response = getPartTimeStaffPayment(staffUsername, start, end, staffPayment);
        }
        return response;
    }

    private StaffPaymentResponse getStaffInfo(String staffUsername) {
        StaffPaymentResponse response;
        Account account = accountRepository.findByUsername(staffUsername).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        response = StaffPaymentResponse.builder()
                .userId(account.getIdAccount())
                .role(account.getRole())
                .username(staffUsername)
                .name(account.getName())
                .phone(account.getPhone())
                .workStartDate(account.getCreatedAt().toString())
                .baseSalary(0)
                .shifts(0)
                .payment(0)
                .type(null)
                .bankAccountNumber(null)
                .bank(null)
                .build();
        return response;
    }

    private StaffPaymentResponse getFullTimeStaffPayment(String staffUsername, Timestamp start, Timestamp end, StaffPayment staffPayment) {
        StaffPaymentResponse response;
        // count number of work day
        int workDays = workDayRepository
                .countWorkDaysByAccount_UsernameAndWorkDateBetween(staffUsername, start, end)
                .orElse(0);
        Account account = accountRepository.findByUsername(staffUsername).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        // payment = work day * salary
        response = StaffPaymentResponse.builder()
                .userId(account.getIdAccount())
                .role(account.getRole())
                .username(staffUsername)
                .name(account.getName())
                .phone(account.getPhone())
                .workStartDate(account.getCreatedAt().toString())
                .baseSalary(staffPayment.getSalary())
                .shifts(workDays)
                .payment(workDays * staffPayment.getSalary())
                .type(staffPayment.getType())
                .bankAccountNumber(staffPayment.getBankAccountNumber())
                .bank(staffPayment.getBank())
                .build();
        return response;
    }

    private StaffPaymentResponse getPartTimeStaffPayment(String staffUsername, Timestamp start, Timestamp end, StaffPayment staffPayment) {
        StaffPaymentResponse response;
        // get all work day of month
        List<WorkDay> workDays = workDayRepository
                .findAllByAccount_UsernameAndWorkDateBetween(staffUsername, start, end)
                .orElse(new ArrayList<>());
        // calculate total hours
        int totalHours = 0;
        for(var workDay : workDays) {
            totalHours += (int) getWorkHourPerDay(workDay);
        }
        Account account = accountRepository.findByUsername(staffUsername).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        // payment = hours * salary
        response = StaffPaymentResponse.builder()
                .username(staffUsername)
                .name(account.getName())
                .baseSalary(staffPayment.getSalary())
                .role(account.getRole())
                .shifts(totalHours)
                .phone(account.getPhone())
                .workStartDate(account.getCreatedAt().toString())
                .userId(account.getIdAccount())
                .payment(totalHours * staffPayment.getSalary())
                .type(staffPayment.getType())
                .bankAccountNumber(staffPayment.getBankAccountNumber())
                .bank(staffPayment.getBank())
                .build();
        return response;
    }

    private long getWorkHourPerDay(WorkDay workDay) {
        return Duration.between(workDay.getStartTime().toLocalTime(), workDay.getEndTime().toLocalTime())
                .toHours();
    }

    public List<StaffPaymentResponse> getAllStaffPayments(String idRestaurant, int month, int year) {
        // all staff username of this restaurant
        List<String> usernames = accountRepository
                .findUsernamesByRestaurantAndStatus(idRestaurant, AccountStatus.Active.name())
                .orElse(new ArrayList<>());

        List<StaffPaymentResponse> responses = new ArrayList<>();
        for(var username : usernames) {
            responses.add(getStaffSalary(username, month, year));
        }
        return responses;
    }

    public String updateStaffPaymentInfo(PaymentInfoRequest request) {
        Account staff = accountRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(ownerCheckingService.iStaffOwner(staff)){
            // get payment info
            StaffPayment payment = staffPaymentRepository.findByAccount_Username(staff.getUsername()).orElse(null);
            if(payment == null){
                payment = StaffPayment.builder()
                        .account(staff)
                        .build();
            }
            payment.setBank(request.getBank());
            payment.setType(request.getType());
            payment.setSalary(request.getSalary());
            payment.setBankAccountNumber(request.getBankAccountNumber());
            staffPaymentRepository.save(payment);
            return "OK";
        } else {
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
    }
}

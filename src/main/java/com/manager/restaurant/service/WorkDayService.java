package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.CreateWorkDayRequest;
import com.manager.restaurant.dto.request.WorkDayRequest;
import com.manager.restaurant.dto.response.WorkDayResponse;
import com.manager.restaurant.entity.*;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.AccountRepository;
import com.manager.restaurant.repository.StaffPaymentRepository;
import com.manager.restaurant.repository.WorkDayRepository;
import com.manager.restaurant.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class WorkDayService {
    final WorkDayRepository workDayRepository;
    final ManagerCheckingService managerCheckingService;
    final AccountRepository accountRepository;
    final StaffPaymentRepository staffPaymentRepository;

    public WorkDayResponse getById(String idWorkDay) {
        if(!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        var workDay = workDayRepository.getReferenceById(idWorkDay);
        if(workDay.getIdWorkDay() == null) throw new BadException(ErrorCode.INVALID_KEY);
        return new WorkDayResponse(workDay);
    }


    public List<WorkDayResponse> getByDate(String dateISOString, String staffId) {
        Account account = validateAccountForGet(staffId);
        String dateString = dateISOString.split("T")[0];
        Timestamp start = Timestamp.valueOf(dateString + " 00:00:00");
        Timestamp end = Timestamp.valueOf(dateString + " 23:59:59");

        List<WorkDay> workDays = workDayRepository.findAllByAccountAndWorkDateBetween(account, start, end)
                .orElseThrow(() -> new BadException(ErrorCode.NOT_FOND));
        return WorkDayResponse.ToList(workDays);
    }

    public List<WorkDayResponse> getByTimeSpan(String dateISOStart, String dateISOEnd, String staffId) {
        Account account = validateAccountForGet(staffId);
        String dateStartString = dateISOStart.split("T")[0];
        String dateEndString = dateISOEnd.split("T")[0];
        Timestamp start = Timestamp.valueOf(dateStartString + " 00:00:00");
        Timestamp end = Timestamp.valueOf(dateEndString + " 23:59:59");

        List<WorkDay> workDays = workDayRepository.findAllByAccountAndWorkDateBetween(account, start, end)
                .orElseThrow(() -> new BadException(ErrorCode.NOT_FOND));
        return WorkDayResponse.ToList(workDays);
    }

    public List<WorkDayResponse> getByMonth(int month, int year, String staffId) {
        Account account = validateAccountForGet(staffId);

        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);

        Timestamp start = Timestamp.valueOf(startOfMonth);
        Timestamp end = Timestamp.valueOf(endOfMonth);

        List<WorkDay> workDays = workDayRepository.findAllByAccountAndWorkDateBetween(account, start, end)
                .orElseThrow(() -> new BadException(ErrorCode.NOT_FOND));
        return WorkDayResponse.ToList(workDays);
    }

    private Account validateAccountForGet(String staffId){
        Account account = accountRepository.findByUsername(SecurityUtils.getCurrentUsername()).orElseThrow(
                ()-> new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(!account.getIdAccount().equals(staffId) &&
                !account.getRole().equals(AccountRole.Manager.name()) &&
                !account.getRole().equals(AccountRole.Owner.name())
        )
            throw new BadException(ErrorCode.ACCESS_DENIED);
        if(!account.getIdAccount().equals(staffId)) {
            account = accountRepository.findByIdAccount(staffId).orElseThrow(
                    ()-> new BadException(ErrorCode.USER_NOT_EXISTED));
        }
        return account;
    }

    public String createWorkDay(CreateWorkDayRequest request) {
        if(!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        var account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadException(ErrorCode.INVALID_KEY));
        // Get staff payment to get payment type
        StaffPayment staffPayment = staffPaymentRepository.findByAccount_Username(request.getUsername())
                .orElseThrow();
        if(staffPayment.getType().equals(PaymentType.FullTime.name())){
            saveWorkDaysForFullTime(getWorkDaysExcludingDayOff(request.getDayOffs()), account);
        }
        else if(staffPayment.getType().equals(PaymentType.PartTime.name())){
            saveWorkDaysForPartTime(request, account);
        }

        return "Ok";
    }


    private void saveWorkDaysForPartTime(CreateWorkDayRequest request, Account account) {
        List<WorkDay> workDays = new ArrayList<>();
        var startTimes = request.getTimeStarts();
        var endTimes = request.getTimeEnds();
        var workDates = request.getWorkDays();
        try {
            for(int i = 0; i < startTimes.size(); i++){
                workDays.add(WorkDay.builder()
                        .account(account)
                        .workDate(Timestamp.valueOf(LocalDate.parse(workDates.get(i)).atStartOfDay()))
                        .startTime(Time.valueOf(startTimes.get(i)))
                        .endTime(Time.valueOf(endTimes.get(i)))
                        .build());
            }
        } catch (Exception ex){
            throw new BadException(ErrorCode.TYPE_NOT_MATCH);
        }
        workDayRepository.saveAll(workDays);
    }

    private void saveWorkDaysForFullTime(List<Timestamp> workDates, Account account) {
        List<WorkDay> workDays = new ArrayList<>();
        for (var workDate : workDates) {
            workDays.add(WorkDay.builder()
                    .account(account)
                    .workDate(workDate)
                    .build());
        }
        workDayRepository.saveAll(workDays);
    }

    private List<Timestamp> getWorkDaysExcludingDayOff(List<String> holidaysISO) {
        if(holidaysISO == null || holidaysISO.isEmpty()) return null;
        Set<LocalDateTime> holidays;
        try {
            holidays = holidaysISO.stream()
                    .map(LocalDate::parse)
                    .map(date -> LocalDateTime.of(date, LocalTime.MIN))
                    .collect(Collectors.toSet());
        } catch (Exception ex){
            throw new BadException(ErrorCode.TYPE_NOT_MATCH);
        }

        // get month, year
        String dateString = holidaysISO.getFirst().split("T")[0];
        int year = Integer.parseInt(dateString.split("-")[0]);
        int month = Integer.parseInt(dateString.split("-")[1]);

        List<Timestamp> workDays = new ArrayList<>();
        LocalDateTime firstDay = LocalDateTime.of(year, month, 1,0,0);
        LocalDateTime lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());

        for (LocalDateTime date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (!holidays.contains(date)) {
                workDays.add(Timestamp.valueOf(date));
            }
        }

        return workDays;
    }

    public String updateWorkDay(WorkDayRequest request) {
        if(!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        if(request.getIdWorkDay() == null) throw new BadException(ErrorCode.INVALID_KEY);
        // get workday and update
        var workDay = workDayRepository.getReferenceById(request.getIdWorkDay());
        if(workDay.getIdWorkDay() == null) throw new BadException(ErrorCode.NOT_FOND);
        workDay.setWorkDate(Timestamp.from(Instant.parse(request.getWorkDate())));
        if(request.getStartTime() != null && !request.getStartTime().isBlank())
            workDay.setStartTime(Time.valueOf(request.getStartTime()));
        if(request.getEndTime() != null && !request.getEndTime().isBlank())
            workDay.setEndTime(Time.valueOf(request.getEndTime()));
        workDayRepository.save(workDay);
        return "Ok";
    }

    public String deleteWorkDay(String idWorkDay) {
        //check role
        if(!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        workDayRepository.deleteById(idWorkDay);
        return "Ok";
    }
}

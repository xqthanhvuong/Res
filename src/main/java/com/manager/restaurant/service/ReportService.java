package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.ReportRequest;
import com.manager.restaurant.dto.response.AdvancedReportResponse;
import com.manager.restaurant.dto.response.ReportImageResponse;
import com.manager.restaurant.dto.response.ReportResponse;
import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.ReportImage;
import com.manager.restaurant.entity.WorkDay;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.ReportImageRepository;
import com.manager.restaurant.repository.ReportRepository;
import com.manager.restaurant.repository.WorkDayRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ReportService {

    ReportRepository reportRepository;
    WorkDayRepository workDayRepository;
    ManagerCheckingService managerCheckingService;
    StaffCheckingService staffCheckingService;
    ReportImageService reportImageService;
    ReportImageRepository reportImageRepository;

    public ReportResponse getReport(String idReport) {
        if (staffCheckingService.isStaff()) throw new BadException(ErrorCode.ACCESS_DENIED);
        var report = reportRepository.getReferenceById(idReport);
        return ReportResponse.builder()
                .idReport(report.getIdReport())
                .note(report.getNote())
                .reportImages(ReportImageResponse.toSet(report.getReportImages()))
                .build();
    }

    public String createReport(ReportRequest request) {
        if (!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        if(request == null || request.getIdWorkDay() == null) throw new BadException(ErrorCode.INVALID_KEY);
        Report report = Report.builder()
                .idReport(UUID.randomUUID().toString())
                .workDay(workDayRepository.getReferenceById(request.getIdWorkDay()))
                .note(request.getNote())
                .build();
        reportRepository.save(report);
        reportImageService.addReportImages(request.getImageUrls(), report);
        return "ok";
    }

    public String updateRepost(ReportRequest request) {
        if (!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        if(request == null || request.getIdReport() == null || request.getIdWorkDay() == null) throw new BadException(ErrorCode.INVALID_KEY);
        Report report = Report.builder()
                .idReport(request.getIdReport())
                .workDay(workDayRepository.getReferenceById(request.getIdWorkDay()))
                .note(request.getNote())
                .build();
        reportRepository.save(report);

        // Delete in report images where report id = :reportId
        reportImageService.deleteAllByReport(report);
        reportImageService.addReportImages(request.getImageUrls(), report);
        return "ok";
    }

    public String delete(String idReport) {
        if (!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        reportRepository.deleteById(idReport);
        return "ok";
    }

    public ReportResponse getReportByWorkday(String idWorkday) {
        if(!managerCheckingService.isManagerOrOwner()) throw new BadException(ErrorCode.ACCESS_DENIED);
        WorkDay workDay = workDayRepository.findById(idWorkday).orElseThrow(
                () -> new BadException(ErrorCode.WORKDAY_NOT_EXISTED)
        );
        Report report = reportRepository.findByWorkDay(workDay);
        if(ObjectUtils.isNotEmpty(report)){
            Set<ReportImage> images = reportImageRepository.findAllByReport(report);
            return ReportResponse.builder()
                    .idReport(report.getIdReport())
                    .note(report.getNote())
                    .reportImages(ReportImageResponse.toSet(images))
                    .build();
        }else {
            return ReportResponse.builder().build();
        }
    }

    public List<AdvancedReportResponse> getAllReportByIdRestaurant(String idRestaurant) {
        return reportRepository.findAllByRestaurantId(idRestaurant).orElse(new ArrayList<>());
    }
}

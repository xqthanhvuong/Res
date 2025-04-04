package com.manager.restaurant.service;

import com.manager.restaurant.dto.request.ReportRequest;
import com.manager.restaurant.dto.response.ReportImageResponse;
import com.manager.restaurant.dto.response.ReportResponse;
import com.manager.restaurant.entity.Report;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.repository.ReportRepository;
import com.manager.restaurant.repository.WorkDayRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ReportService {

    final ReportRepository reportRepository;
    final WorkDayRepository workDayRepository;
    final ManagerCheckingService managerCheckingService;
    final StaffCheckingService staffCheckingService;
    final ReportImageService reportImageService;

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
}

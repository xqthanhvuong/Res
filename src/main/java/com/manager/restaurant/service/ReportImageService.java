package com.manager.restaurant.service;

import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.ReportImage;
import com.manager.restaurant.repository.ReportImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ReportImageService {

    final ReportImageRepository reportImageRepository;
    public void addReportImages(List<String> urls, Report report) {
        List<ReportImage> reportImages = new ArrayList<>();
        for(var url : urls) {
            reportImages.add(ReportImage.builder()
                    .idReportImage(UUID.randomUUID().toString())
                    .report(report)
                    .url(url)
                    .build());
        }
        reportImageRepository.saveAll(reportImages);
    }

    public void deleteAllByReport(Report report) {
        reportImageRepository.deleteAllByReport(report);
    }
}

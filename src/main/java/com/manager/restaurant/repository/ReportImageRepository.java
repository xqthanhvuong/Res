package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, String> {

    void deleteAllByReport(Report report);

    Set<ReportImage> findAllByReport(Report report);
}

package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.ReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, String> {

    void deleteAllByReport(Report report);
}

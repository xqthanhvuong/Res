package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    Report findByWorkDay(WorkDay workDay);
}

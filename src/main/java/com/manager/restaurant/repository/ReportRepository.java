package com.manager.restaurant.repository;

import com.manager.restaurant.dto.response.AdvancedReportResponse;
import com.manager.restaurant.entity.Report;
import com.manager.restaurant.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    Report findByWorkDay(WorkDay workDay);

    @Query("""
        SELECT new com.manager.restaurant.dto.response.AdvancedReportResponse(r.idReport, r.note, acc.name, wd.workDate)
        FROM Report r
        JOIN r.workDay wd
        JOIN wd.account acc
        WHERE acc.restaurant.idRestaurant = :resId
    """)
    List<AdvancedReportResponse> findAllByRestaurantId(@Param("resId") String idRestaurant);
}

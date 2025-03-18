package com.manager.restaurant.repository;

import com.manager.restaurant.entity.Account;
import com.manager.restaurant.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkDayRepository extends JpaRepository<WorkDay, String> {
    Optional<List<WorkDay>> findAllByAccountAndWorkDateBetween(Account account, Timestamp start, Timestamp end);

    Optional<List<WorkDay>> findAllByAccount_UsernameAndWorkDateBetween(String username, Timestamp start, Timestamp end);
    Optional<Integer> countWorkDaysByAccount_UsernameAndWorkDateBetween(String username, Timestamp start, Timestamp end);
}

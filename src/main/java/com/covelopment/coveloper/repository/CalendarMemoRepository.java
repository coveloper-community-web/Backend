package com.covelopment.coveloper.repository;

import com.covelopment.coveloper.entity.CalendarMemo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarMemoRepository extends JpaRepository<CalendarMemo, Long> {
    Optional<CalendarMemo> findByDate(LocalDate date);

    List<CalendarMemo> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
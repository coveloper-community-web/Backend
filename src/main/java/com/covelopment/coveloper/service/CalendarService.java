package com.covelopment.coveloper.service;

import com.covelopment.coveloper.dto.CalendarMemoDTO;
import com.covelopment.coveloper.entity.CalendarMemo;
import com.covelopment.coveloper.repository.CalendarMemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final CalendarMemoRepository calendarMemoRepository;

    public CalendarService(CalendarMemoRepository calendarMemoRepository) {
        this.calendarMemoRepository = calendarMemoRepository;
    }

    @Transactional(readOnly = true)
    public List<CalendarMemoDTO> getMemosForMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<CalendarMemo> memos = calendarMemoRepository.findByDateBetween(startDate, endDate);
        return memos.stream()
                .map(memo -> new CalendarMemoDTO(memo.getDate(), memo.getMemo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CalendarMemoDTO getMemoByDate(LocalDate date) {
        return calendarMemoRepository.findByDate(date)
                .map(memo -> new CalendarMemoDTO(memo.getDate(), memo.getMemo()))
                .orElse(null); // 메모가 없으면 null 반환
    }

    @Transactional
    public CalendarMemoDTO saveOrUpdateMemo(CalendarMemoDTO memoDTO) {
        CalendarMemo memo = calendarMemoRepository.findByDate(memoDTO.getDate())
                .orElse(new CalendarMemo());
        memo.setDate(memoDTO.getDate());
        memo.setMemo(memoDTO.getMemo());
        CalendarMemo savedMemo = calendarMemoRepository.save(memo);
        return new CalendarMemoDTO(savedMemo.getDate(), savedMemo.getMemo());
    }
}
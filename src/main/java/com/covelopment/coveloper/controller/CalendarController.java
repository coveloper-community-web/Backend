package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.dto.CalendarMemoDTO;
import com.covelopment.coveloper.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // 특정 월의 메모 목록 조회
    @GetMapping("/memos")
    public ResponseEntity<List<CalendarMemoDTO>> getMemosForMonth(@RequestParam int year, @RequestParam int month) {
        List<CalendarMemoDTO> memos = calendarService.getMemosForMonth(year, month);
        return ResponseEntity.ok(memos);
    }

    // 특정 날짜의 메모 조회
    @GetMapping("/memo")
    public ResponseEntity<CalendarMemoDTO> getMemoByDate(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        CalendarMemoDTO memo = calendarService.getMemoByDate(localDate);
        if (memo != null) {
            return ResponseEntity.ok(memo);
        } else {
            return ResponseEntity.noContent().build(); // 메모가 없을 경우 204 No Content 반환
        }
    }

    // 특정 날짜의 메모 생성 또는 업데이트
    @PostMapping("/memo")
    public ResponseEntity<CalendarMemoDTO> saveOrUpdateMemo(@RequestBody CalendarMemoDTO memoDTO) {
        CalendarMemoDTO savedMemo = calendarService.saveOrUpdateMemo(memoDTO);
        return ResponseEntity.ok(savedMemo);
    }
}
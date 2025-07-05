package com.kagoshima.api.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.ReportDueDateDto;
import com.kagoshima.api.mapper.ReportDueDateMapper;
import com.kagoshima.entity.ReportDueDate;
import com.kagoshima.service.ReportDueDateService;

@RestController
@RequestMapping("/api/report-due-dates")
public class ReportDueDateApiController {

	private final ReportDueDateService reportDueDateService;
	private final ReportDueDateMapper reportDueDateMapper;

	public ReportDueDateApiController(ReportDueDateService reportDueDateService,
			ReportDueDateMapper reportDueDateMapper) {
		this.reportDueDateService = reportDueDateService;
		this.reportDueDateMapper = reportDueDateMapper;
	}

	// 一覧取得
	@GetMapping
	public List<ReportDueDateDto> getAll() {
		return reportDueDateService.findAllDesc().stream().map(reportDueDateMapper::toDto).collect(Collectors.toList());
	}

	// 新規作成（年毎に一括作成）
	@PostMapping("/yearly/{year}")
	public ResponseEntity<?> createYearlyDueDates(@PathVariable int year) {
		try {
			LocalTime defaultTime = LocalTime.of(18, 0); // 18:00 をデフォルト時刻に
			List<ReportDueDate> list = reportDueDateService.createYearlyDueDates(year, defaultTime);
			return ResponseEntity.ok(list);
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
		}
	}

	// 削除（年を指定して一括削除）
	@DeleteMapping("/yearly/{year}")
	public ResponseEntity<Void> deleteYearlyDueDates(@PathVariable int year) {
		reportDueDateService.deleteByYear(year);
		return ResponseEntity.noContent().build(); // HTTP 204 No Content
	}

	// 提出期日更新
	@PutMapping
	public ResponseEntity<?> updateReportDueDates(@RequestBody List<ReportDueDateDto> dtoList) {
		try {
			List<ReportDueDate> entities = dtoList.stream().map(reportDueDateMapper::toEntity).toList();

			List<ReportDueDate> updatedList = reportDueDateService.updateReportDueDate(entities);
			List<ReportDueDateDto> updatedDtoList = updatedList.stream().map(reportDueDateMapper::toDto).toList();

			return ResponseEntity.ok(updatedDtoList);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 指定した年月の提出期日の日時を返す
	@GetMapping("/due-date")
	public ResponseEntity<LocalDateTime> getDueDateByYearMonth(@RequestParam String yearMonth) {
		// 例: "2025-07" → YearMonth に変換
		YearMonth ym = YearMonth.parse(yearMonth);
		Optional<ReportDueDate> optional = reportDueDateService.findByYearMonth(ym);

		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get().getDueDate());
		} else {
			// 期日未設定の場合は月末18:00を返す
			LocalDateTime defaultDueDate = ym.atEndOfMonth().atTime(18, 0);
			return ResponseEntity.ok(defaultDueDate);
		}
	}

}

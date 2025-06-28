package com.kagoshima.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kagoshima.entity.ReportDueDate;
import com.kagoshima.repository.ReportDueDateRepository;

@Service
@Transactional
public class ReportDueDateService {

	private final ReportDueDateRepository reportDueDateRepository;

	public ReportDueDateService(ReportDueDateRepository reportDueDateRepository) {
		this.reportDueDateRepository = reportDueDateRepository;
	}

	// 全取得（年月の降順）
	public List<ReportDueDate> findAllDesc() {
		return reportDueDateRepository.findAllByOrderByYearmonthDesc();
	}

	// 年月で1件取得
	public Optional<ReportDueDate> findByYearMonth(YearMonth yearMonth) {
		return reportDueDateRepository.findByYearmonth(yearMonth);
	}

	// 年月で存在確認
	public boolean existsByYearMonth(YearMonth yearMonth) {
		return reportDueDateRepository.existsByYearmonth(yearMonth);
	}

	// 新規作成
	public List<ReportDueDate> createYearlyDueDates(int year, LocalTime defaultTime) {
		// その年のいずれかの月がすでに存在するかチェック
		boolean anyExists = IntStream.rangeClosed(1, 12).mapToObj(month -> YearMonth.of(year, month))
				.anyMatch(this::existsByYearMonth);

		if (anyExists) {
			throw new IllegalStateException("指定された年の提出期日がすでに存在しています");
		}

		// 存在しなければ作成
		List<ReportDueDate> createdList = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			YearMonth ym = YearMonth.of(year, month);
			ReportDueDate dueDate = new ReportDueDate();
			dueDate.setYearmonth(ym);
			dueDate.setDueDate(LocalDateTime.of(ym.atEndOfMonth(), defaultTime));
			createdList.add(reportDueDateRepository.save(dueDate));
		}
		return createdList;
	}

	// 年を指定してその年のすべての提出期日を削除
	public void deleteByYear(int year) {
		IntStream.rangeClosed(1, 12).mapToObj(month -> YearMonth.of(year, month))
				.forEach(ym -> reportDueDateRepository.findByYearmonth(ym).ifPresent(reportDueDateRepository::delete));
	}

	// 提出期日更新
	public List<ReportDueDate> updateReportDueDate(List<ReportDueDate> dueDates) {
		List<YearMonth> missing = dueDates.stream().map(ReportDueDate::getYearmonth)
				.filter(ym -> !existsByYearMonth(ym)).toList();

		if (!missing.isEmpty()) {
			throw new IllegalArgumentException("以下の年月は登録されていないため更新できません: " + missing);
		}

		List<ReportDueDate> updated = new ArrayList<>();
		for (ReportDueDate input : dueDates) {
			ReportDueDate existing = reportDueDateRepository.findByYearmonth(input.getYearmonth()).get();
			existing.setDueDate(input.getDueDate());
			updated.add(reportDueDateRepository.save(existing));
		}
		return updated;
	}

}

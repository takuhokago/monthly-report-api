package com.kagoshima.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

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

	// 新規・更新登録
	public ReportDueDate save(ReportDueDate reportDueDate) {
		return reportDueDateRepository.save(reportDueDate);
	}

	// IDで取得
	public Optional<ReportDueDate> findById(Integer id) {
		return reportDueDateRepository.findById(id);
	}
}

package com.kagoshima.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kagoshima.entity.ReportDueDate;

public interface ReportDueDateRepository extends JpaRepository<ReportDueDate, Integer> {

	// yearmonth で取得
	Optional<ReportDueDate> findByYearmonth(YearMonth yearmonth);

	// 存在確認
	boolean existsByYearmonth(YearMonth yearmonth);

	// 降順で一覧を取得（例: 最近の締切を上に）
	java.util.List<ReportDueDate> findAllByOrderByYearmonthDesc();
}

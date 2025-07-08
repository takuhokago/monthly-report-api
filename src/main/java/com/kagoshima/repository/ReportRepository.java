package com.kagoshima.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
	List<Report> findByEmployee(Employee employee);

	Optional<Report> findByEmployeeAndReportMonth(Employee employee, YearMonth reportMonth);
}
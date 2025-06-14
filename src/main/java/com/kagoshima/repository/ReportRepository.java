package com.kagoshima.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByEmployee(Employee employee);
}
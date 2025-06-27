package com.kagoshima.entity;

import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "report_due_date")
@Data
public class ReportDueDate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonFormat(pattern = "yyyy-MM")
	@Column(nullable = false, unique = true)
	private YearMonth yearmonth;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private java.time.LocalDateTime dueDate;
}

package com.kagoshima.api.dto;

import java.time.LocalDateTime;
import java.time.YearMonth;

import lombok.Data;

@Data
public class ReportDueDateDto {
	private Integer id;
	private YearMonth yearmonth;
	private LocalDateTime dueDate;
}

package com.kagoshima.api.mapper;

import org.springframework.stereotype.Component;

import com.kagoshima.api.dto.ReportDueDateDto;
import com.kagoshima.entity.ReportDueDate;

@Component
public class ReportDueDateMapper {

	public ReportDueDateDto toDto(ReportDueDate entity) {
		ReportDueDateDto dto = new ReportDueDateDto();
		dto.setId(entity.getId());
		dto.setYearmonth(entity.getYearmonth());
		dto.setDueDate(entity.getDueDate());
		return dto;
	}

	public ReportDueDate toEntity(ReportDueDateDto dto) {
		ReportDueDate entity = new ReportDueDate();
		entity.setId(dto.getId());
		entity.setYearmonth(dto.getYearmonth());
		entity.setDueDate(dto.getDueDate());
		return entity;
	}
}

package com.kagoshima.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	// 新規・更新保存
	@PostMapping
	public ReportDueDateDto save(@RequestBody ReportDueDateDto dto) {
		ReportDueDate entity = reportDueDateMapper.toEntity(dto);
		ReportDueDate saved = reportDueDateService.save(entity);
		return reportDueDateMapper.toDto(saved);
	}

}

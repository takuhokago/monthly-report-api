package com.kagoshima.api.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.ReportDto;
import com.kagoshima.api.dto.ReportResponse;
import com.kagoshima.api.mapper.ReportMapper;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ReportService;
import com.kagoshima.service.UserDetail;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

	private final ReportService reportService;
	private final EmployeeService employeeService;

	@Autowired
	public ReportApiController(ReportService reportService, EmployeeService employeeService) {
		this.reportService = reportService;
		this.employeeService = employeeService;
	}

	@GetMapping
	public ReportListResponse getReports(@AuthenticationPrincipal UserDetail userDetail) {
		List<Report> allReports;

		if (userDetail.getEmployee().getRole() == Role.ADMIN) {
			// 管理者は全件取得
			allReports = reportService.findAll();
		} else {
			// 一般社員は同じ部署の人たちのレポートを取得
			List<Employee> sameDeptEmployees = employeeService
					.findByDepartment(userDetail.getEmployee().getDepartment());
			allReports = sameDeptEmployees.stream().flatMap(emp -> reportService.findByEmployee(emp).stream())
					.collect(Collectors.toList());
		}

		// 年月リスト（降順）
		Set<YearMonth> dateSet = allReports.stream().map(Report::getReportMonth)
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.reverseOrder())));

		// 自身の過去レポートがあるかどうか
		boolean isPastCheck = !reportService.findByEmployee(userDetail.getEmployee()).isEmpty();

		return new ReportListResponse(allReports.size(), allReports.stream().map(ReportMapper::toDto).toList(),
				dateSet.stream().toList(), isPastCheck);
	}

	public record ReportListResponse(int listSize, List<ReportDto> reportList, List<YearMonth> dateSet,
			boolean isPastCheck) {
	}

	@GetMapping("/{id}")
	public ReportResponse getReportById(@PathVariable String id) {

		Report report = reportService.findById(id);

		return new ReportResponse(ReportMapper.toDto(report));
	}

	@PostMapping
	public ResponseEntity<ReportResponse> createReport(@RequestBody ReportDto dto,
			@AuthenticationPrincipal UserDetail userDetail) {

		Employee employee = userDetail.getEmployee();
		Report saved = reportService.save(dto, employee);

		return ResponseEntity.ok(new ReportResponse(ReportMapper.toDto(saved)));
	}

}

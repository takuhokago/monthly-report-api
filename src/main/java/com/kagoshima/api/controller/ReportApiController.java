package com.kagoshima.api.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.ReportDto;
import com.kagoshima.api.dto.ReportResponse;
import com.kagoshima.api.mapper.ReportMapper;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ExcelService;
import com.kagoshima.service.ReportService;
import com.kagoshima.service.UserDetail;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

	private final ReportService reportService;
	private final EmployeeService employeeService;
	private final ExcelService excelService;

	@Autowired
	public ReportApiController(ReportService reportService, EmployeeService employeeService,
			ExcelService excelService) {
		this.reportService = reportService;
		this.employeeService = employeeService;
		this.excelService = excelService;
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
				.collect(Collectors.toCollection(() -> new TreeSet<YearMonth>(Comparator.reverseOrder())));

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

	@PutMapping("/{id}")
	public ResponseEntity<ReportResponse> updateReport(@PathVariable String id, @RequestBody ReportDto dto,
			@AuthenticationPrincipal UserDetail userDetail) {

		// 元レポートを取得（nullチェックはservice内でも行うが、念のため）
		Report existing = reportService.findById(id);
		if (existing == null) {
			return ResponseEntity.notFound().build();
		}

		// ログインユーザーを取得
		Employee loginEmployee = userDetail.getEmployee();

		// 本人 or 管理者 以外は403エラー
		if (!existing.getEmployee().getCode().equals(loginEmployee.getCode())
				&& loginEmployee.getRole() != Role.ADMIN) {
			return ResponseEntity.status(403).build();
		}

		// dtoにIDをセット（URLのIDを信頼）
		dto.setId(existing.getId());

		// 更新処理を実行（EmployeeはMapper内で既存のを使用）
		Report updated = reportService.update(dto);

		// レスポンス用DTOに変換して返す
		return ResponseEntity.ok(new ReportResponse(ReportMapper.toDto(updated)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteReport(@PathVariable String id) {
		reportService.delete(id); // 論理削除を呼び出す
		return ResponseEntity.noContent().build(); // HTTP 204 No Content を返す
	}

	@PatchMapping("/{id}/approval")
	public ResponseEntity<ReportResponse> approveReport(@PathVariable String id, @RequestParam boolean approve) {
		Report updated = reportService.approve(id, approve);
		return ResponseEntity.ok(new ReportResponse(ReportMapper.toDto(updated)));
	}

	@PatchMapping("/{id}/comment")
	public ResponseEntity<Void> commentOnReport(@PathVariable String id, @RequestBody Map<String, String> requestBody) {
		String comment = requestBody.get("comment");
		reportService.comment(id, comment);
		return ResponseEntity.noContent().build(); // 成功時は204を返す
	}

	@GetMapping("/latest")
	public ResponseEntity<ReportDto> getLatestReport(@AuthenticationPrincipal UserDetail userDetail) {
		Report latestReport = reportService.findLatestByEmployee(userDetail.getEmployee());
		if (latestReport == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(ReportMapper.toDto(latestReport));
	}

	@PostMapping("/export")
	public void exportReport(@RequestParam("reportId") String reportId, HttpServletResponse response,
			@AuthenticationPrincipal UserDetail userDetail) {

		try {
			Report report = reportService.findById(reportId);

			// 本人 or 管理者 以外は403
			Employee loginEmployee = userDetail.getEmployee();
			if (!report.getEmployee().getCode().equals(loginEmployee.getCode())
					&& loginEmployee.getRole() != Role.ADMIN) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
				return;
			}

			Workbook workbook = excelService.createWorkbookWithReport(report);

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			String encodedFileName = URLEncoder.encode(excelService.getFileName(report), StandardCharsets.UTF_8)
					.replace("+", "%20");

			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

			try (OutputStream out = response.getOutputStream()) {
				workbook.write(out);
			}
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "エクスポートに失敗しました");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

}

package com.kagoshima.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.constants.ErrorMessage;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ExcelService;
import com.kagoshima.service.ReportService;
import com.kagoshima.service.UserDetail;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("reports")
public class ReportController {

	private final ReportService reportService;
	private final EmployeeService employeeService;
	private final ExcelService excelService;

	@Autowired
	public ReportController(ReportService reportService, EmployeeService employeeService, ExcelService excelService) {
		this.reportService = reportService;
		this.employeeService = employeeService;
		this.excelService = excelService;
	}

	// 月報一覧画面
	@GetMapping
	public String list(Model model, @AuthenticationPrincipal UserDetail userDetail, YearMonth yearMonth) {
		// 表示用
		List<Report> reportList = new ArrayList<>();
		// すべての年月の報告書
		List<Report> repListAll = new ArrayList<>();

		YearMonth yearMonth4List = YearMonth.now();
		if (yearMonth != null) {
			yearMonth4List = yearMonth;
		}

		if (userDetail.getEmployee().getRole().equals(Role.ADMIN)) {
			// 管理者権限の場合、すべてのレポートを取得
			repListAll = reportService.findAll();
			// 今月分のみ表示するように変更
			List<Report> thisMonthReportList = reportService.getSpecifiedMonthReport(repListAll, yearMonth4List);
			reportList.addAll(thisMonthReportList);
		} else {
			// 一般権限の場合、ログインユーザと同じ所属のレポートを取得
			List<Employee> employeeList = employeeService.findByDepartment(userDetail.getEmployee().getDepartment());
			repListAll = new ArrayList<>();
			for (Employee employee : employeeList) {
				List<Report> repListByEmployee = reportService.findByEmployee(employee);
				if (repListByEmployee != null) {
					repListAll.addAll(repListByEmployee);
				}
			}
			// 今月分のみ表示するように変更
			List<Report> thisMonthReportList = reportService.getSpecifiedMonthReport(repListAll, yearMonth4List);
			reportList.addAll(thisMonthReportList);
		}

		// 新しい日付順にソートするためのComparatorを作成
		Comparator<YearMonth> comparator = Comparator.reverseOrder();
		// 表示月選択用
		TreeSet<YearMonth> dateSet = new TreeSet<>(comparator);
		for (Report rep : repListAll) {
			dateSet.add(rep.getReportMonth());
		}

		// 直近の報告書引き継ぎ用
		boolean isPastCheck = false;
		if (reportService.findByEmployee(userDetail.getEmployee()).size() > 0) {
			isPastCheck = true;
		}

		model.addAttribute("listSize", reportList.size());
		model.addAttribute("reportList", reportList);
		model.addAttribute("dateSet", dateSet);
		model.addAttribute("isPastCheck", isPastCheck);

		return "reports/list";
	}

	// 選択した月の報告書一覧を表示
	@PostMapping(value = "/list")
	public String specifiedMonthReportList(Model model, @AuthenticationPrincipal UserDetail userDetail,
			@RequestParam("selectedDate") YearMonth selectedDate) {
		return list(model, userDetail, selectedDate);
	}

	// 月報新規登録画面
	@PostMapping(value = "/create")
	public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model,
			@RequestParam(name = "pastCheck", required = false) String pastCheck) {
		model.addAttribute("fullName", userDetail.getEmployee().getFullName());
		model.addAttribute("departmentName", userDetail.getEmployee().getDepartment().getName());

		if (pastCheck != null) {
			// 直近の報告書を引き継ぐ場合
			List<Report> reports = reportService.findByEmployee(userDetail.getEmployee());
			if (reports.size() > 0) {
				Collections.sort(reports, Comparator.comparing(Report::getReportMonth));
				Report rep = reports.get(reports.size() - 1);
				model.addAttribute("report", rep);
			}
		}

		return "reports/new";
	}

	// 月報新規登録処理
	@PostMapping(value = "/add")
	public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail,
			Model model) {

		// 入力チェック
		if (res.hasErrors()) {
			return create(report, userDetail, model, null);
		}

		ErrorKinds result = reportService.save(report, userDetail);

		if (ErrorMessage.contains(result)) {
			model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
					ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
			return create(report, userDetail, model, null);
		}

		return "redirect:/reports";
	}

	// 月報詳細画面
	@GetMapping(value = "/{id}/")
	public String detail(@PathVariable String id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
		model.addAttribute("report", reportService.findById(id));
		model.addAttribute("employee", userDetail.getEmployee());
		model.addAttribute("reportId", id);

		return "reports/detail";
	}

	// エクセル出力
	@PostMapping(value = "/export")
	public void export(@RequestParam("reportId") String reportId, HttpServletResponse response) {
		try {
			Report report = reportService.findById(reportId);

			// 書き込み
			Workbook workbook = excelService.createWorkbookWithReport(report);

			// レスポンス設定
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			// ファイル名を UTF-8 でエンコード（スペースや特殊文字も安全に処理）
			String encodedFileName = URLEncoder.encode(excelService.getFileName(report), StandardCharsets.UTF_8).replace("+", "%20");

			// Content-Disposition ヘッダーを設定
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

			// ファイルを書き込んでユーザーにダウンロードさせる
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.close();
			workbook.close();

		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// 月報更新画面
	@GetMapping(value = "/update/{id}/")
	public String edit(@PathVariable String id, Model model, Report report) {
		if (report.getEmployee() == null) {
			// 更新画面を最初に開くときはこっち
			model.addAttribute("report", reportService.findById(id));
		} else {
			// 更新処理に失敗した場合はこっち
			model.addAttribute("report", report);
		}

		return "reports/update";
	}

	// 月報更新処理
	@PostMapping(value = "/update")
	public String update(@Validated Report report, BindingResult res, Model model) {

		// Report内のEmployeeの情報がnullになっているのでここで設定しなおす
		if (report.getEmployee() != null && report.getEmployee().getCode() != null) {
			report.setEmployee(employeeService.findByCode(report.getEmployee().getCode()));
		}

		// 入力チェック
		if (res.hasErrors()) {
			return edit(report.getId().toString(), model, report);
		}
		ErrorKinds result = reportService.update(report);

		if (ErrorMessage.contains(result)) {
			model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
			return edit(report.getId().toString(), model, report);
		}

		return "redirect:/reports";

	}

	// 月報削除処理
	@PostMapping(value = "/{id}/delete")
	public String delete(@PathVariable String id, Model model) {

		reportService.delete(id);

		return "redirect:/reports";
	}

	// コメント追加
	@PostMapping(value = "/comment")
	public String comment(Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
		String id = report.getId().toString();
		String comment = report.getComment();
		reportService.comment(id, comment);

		// 詳細画面に戻る
		return detail(id, userDetail, model);
	}

	// 承認/非承認処理
	@PostMapping(value = "/approve")
	public String approve(Report report, @RequestParam("choice") boolean choice,
			@AuthenticationPrincipal UserDetail userDetail, Model model) {
		String id = report.getId().toString();
		boolean isApprove = choice;
		Report rep = reportService.approve(id, isApprove);

		// 詳細画面に戻る
		return detail(rep.getId().toString(), userDetail, model);
	}

}

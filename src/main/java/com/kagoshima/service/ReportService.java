package com.kagoshima.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kagoshima.api.dto.ReportDto;
import com.kagoshima.api.mapper.ReportMapper;
import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.constants.ErrorMessage;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;
import com.kagoshima.entity.ReportDueDate;
import com.kagoshima.repository.ReportRepository;

@Service
public class ReportService {

	private final ReportRepository reportRepository;
	private final ReportDueDateService reportDueDateService;

	@Autowired
	public ReportService(ReportRepository reportRepository, ReportDueDateService reportDueDateService) {
		this.reportRepository = reportRepository;
		this.reportDueDateService = reportDueDateService;
	}

	// 月報一覧表示処理
	public List<Report> findAll() {
		return reportRepository.findAll();
	}

	// idでレポートを検索
	public Report findById(String id) {
		// findByIdで検索
		Optional<Report> option = reportRepository.findById(Integer.valueOf(id));
		// 取得できなかった場合はnullを返す
		Report report = option.orElse(null);
		return report;
	}

	// 引数の従業員の報告書を取得
	public List<Report> findByEmployee(Employee employee) {
		List<Report> reports = reportRepository.findByEmployee(employee);
		return reports;
	}

	// 指定月の報告書を返す
	public List<Report> getSpecifiedMonthReport(List<Report> reportList, YearMonth yearMonth) {
		List<Report> specigiedMonthReport = new ArrayList<Report>();
		if (yearMonth == null) {
			return specigiedMonthReport;
		}
		for (Report report : reportList) {
			if (report.getReportMonth().equals(yearMonth)) {
				specigiedMonthReport.add(report);
			}
		}

		return specigiedMonthReport;
	}

	// 月報保存
	@Transactional
	public ErrorKinds save(Report report, UserDetail userDetail) {
		// ログインしているユーザーのEmployeeを取得
		Employee employee = userDetail.getEmployee();

		LocalDateTime now = LocalDateTime.now();
		report.setUpdatedAt(now);

		if (report.isCompleteFlg()) {
			report.setSubmittedAt(now);
		} else {
			report.setSubmittedAt(null);
		}

		report.setReportDeadline(report.getReportMonth().atEndOfMonth());

		// 月重複チェック
		ErrorKinds result = reportDateCheck(report, employee);
		if (ErrorKinds.CHECK_OK != result) {
			return result;
		}

		// ログインしているユーザーのEmployeeをReportに登録
		report.setEmployee(employee);

		reportRepository.save(report);

		return ErrorKinds.SUCCESS;

	}

	@Transactional
	public Report save(ReportDto dto, Employee employee) {
		// 提出期日を設定する
		YearMonth yearMonth = dto.getReportMonth();
		ReportDueDate reportDueDate = reportDueDateService.findByYearMonth(yearMonth).orElse(null);

		// DBに保存するreportを生成
		Report report = ReportMapper.toEntity(dto, employee, reportDueDate);

		// 新規登録の初期化処理
		report.setId(null);
		report.setSubmittedAt(LocalDateTime.now());
		report.setUpdatedAt(LocalDateTime.now());
		report.setApprovalFlg(null);
		report.setComment("");

		// 月末日を reportDeadline に設定
		YearMonth ym = dto.getReportMonth();
		report.setReportDeadline(ym.atEndOfMonth());

		// 月重複チェック
		ErrorKinds result = reportDateCheck(report, employee);
		if (result != ErrorKinds.CHECK_OK) {
			throw new RuntimeException(ErrorMessage.getErrorValue(result));
		}

		return reportRepository.save(report);
	}

	// 月報更新
	@Transactional
	public ErrorKinds update(Report report) {

		Employee employee = report.getEmployee();

		// 月重複チェック
		ErrorKinds result = reportDateCheck(report, employee);
		if (ErrorKinds.CHECK_OK != result) {
			return result;
		}

		LocalDateTime now = LocalDateTime.now();
		report.setUpdatedAt(now);

		if (report.isCompleteFlg()) {
			report.setSubmittedAt(now);
		} else {
			report.setSubmittedAt(null);
		}

		report.setReportDeadline(report.getReportMonth().atEndOfMonth());

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}

	// 月報削除
	@Transactional
	public void delete(String id) {

		Report report = findById(id);
		LocalDateTime now = LocalDateTime.now();
		report.setUpdatedAt(now);
		report.setDeleteFlg(true);
	}

	// 月重複チェック
	private ErrorKinds reportDateCheck(Report report, Employee employee) {
		// 引数employeeの月報を取得
		List<Report> reports = reportRepository.findByEmployee(employee);

		if (reports != null) {
			for (Report rep : reports) {
				if (rep.getReportMonth().equals(report.getReportMonth())) {
					// TODO
					if (report.getId() != null) {
						if (report.getId().equals(rep.getId())) {
							// 同一idの月報の場合、月が同じでも無問題
							continue;
						}
					}
					// 同一の月報でない場合かつ同じ月の月報がある場合エラーを返す
					return ErrorKinds.DATECHECK_ERROR;
				}
			}
		}

		return ErrorKinds.CHECK_OK;
	}

	// コメント追加
	@Transactional
	public void comment(String id, String comment) {

		Report report = findById(id);
		report.setComment(comment);

	}

	// 承認/非承認処理
	@Transactional
	public Report approve(String id, boolean isApprove) {

		Report report = findById(id);
		report.setApprovalFlg(isApprove);

		return report;
	}

	@Transactional
	public Report update(ReportDto dto) {
		// IDで元のレポート取得
		Report existing = findById(String.valueOf(dto.getId()));
		if (existing == null) {
			throw new IllegalArgumentException("指定されたレポートが存在しません");
		}

		// 提出期日を更新
		ReportDueDate dueDate = reportDueDateService.findByYearMonth(dto.getReportMonth()).orElse(null);

		// MapperでEntityを再構築（employeeは元のを使用）
		Report report = ReportMapper.toEntity(dto, existing.getEmployee(), dueDate);

		// 更新日時
		report.setUpdatedAt(LocalDateTime.now());

		// 完了していれば提出日時をセット、それ以外はnull
		if (report.isCompleteFlg()) {
			report.setSubmittedAt(LocalDateTime.now());
		} else {
			report.setSubmittedAt(null);
		}

		// 月末日を再設定（フロントから来てないことも想定して保険）
		report.setReportDeadline(report.getReportMonth().atEndOfMonth());

		// コメント、承認フラグなどはフロントからの入力を優先（Mapperが反映済み）

		// 月重複チェック（元IDは保持されているので問題なし）
		ErrorKinds result = reportDateCheck(report, report.getEmployee());
		if (result != ErrorKinds.CHECK_OK) {
			throw new RuntimeException("月重複エラー: " + result);
		}

		// 保存して返却
		return reportRepository.save(report);
	}

	public Report findLatestByEmployee(Employee employee) {
		List<Report> reports = reportRepository.findByEmployee(employee);
		return reports.stream().filter(r -> !r.isDeleteFlg()) // 論理削除されてないもの
				.max((r1, r2) -> r1.getReportMonth().compareTo(r2.getReportMonth())).orElse(null);
	}

	/*
	 * {reportId}のReportを起点に直近{months}か月分のReportリストを返す。
	 */
	public List<Report> getLatestReports(String reportId, int months) {
		Report currentReport = findById(reportId);
		if (currentReport == null || months <= 0) {
			return new ArrayList<>();
		}

		Employee employee = currentReport.getEmployee();
		List<Report> allReports = reportRepository.findByEmployee(employee);

		// 論理削除されていないレポートのみ対象
		List<Report> activeReports = allReports.stream().filter(r -> !r.isDeleteFlg()).toList();

		YearMonth baseMonth = currentReport.getReportMonth();
		List<YearMonth> targetMonths = new ArrayList<>();
		for (int i = months - 1; i >= 0; i--) {
			targetMonths.add(baseMonth.minusMonths(i));
		}

		List<Report> result = new ArrayList<>();
		for (YearMonth ym : targetMonths) {
			for (Report r : activeReports) {
				if (r.getReportMonth().equals(ym)) {
					result.add(r);
					break; // 同じ月の中で1件だけ
				}
			}
		}

		return result;
	}

}
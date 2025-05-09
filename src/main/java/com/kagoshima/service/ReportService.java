package com.kagoshima.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;
import com.kagoshima.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 月報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // idでレポートを検索
    public Report findById(String id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
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
    	if(yearMonth == null) {
    		return specigiedMonthReport;
    	}
    	for(Report report : reportList) {
    		if(report.getReportMonth().equals(yearMonth)) {
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

        if(report.isCompleteFlg()) {
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

        if(report.isCompleteFlg()) {
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

        Report report= findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);
    }

    // 月重複チェック
    private ErrorKinds reportDateCheck(Report report, Employee employee) {
        // 引数employeeの月報を取得
        List<Report> reports = reportRepository.findByEmployee(employee);

        if(reports != null) {
            for(Report rep : reports) {
                if(rep.getReportMonth().equals(report.getReportMonth())) {
                    // TODO
                    if(report.getId() != null) {
                        if(report.getId().equals(rep.getId())) {
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

        Report report= findById(id);
        report.setComment(comment);

    }

    // 承認/非承認処理
    @Transactional
    public Report approve(String id, boolean isApprove) {

        Report report= findById(id);
        report.setApprovalFlg(isApprove);

        return report;
    }


}
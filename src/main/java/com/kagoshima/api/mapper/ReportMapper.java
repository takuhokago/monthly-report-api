package com.kagoshima.api.mapper;

import com.kagoshima.api.dto.ReportDto;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;

public class ReportMapper {

    /** Entity → DTO 変換 */
    public static ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setReportMonth(report.getReportMonth());
        dto.setSubmittedAt(report.getSubmittedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        dto.setContentBusiness(report.getContentBusiness());
        dto.setTimeWorked(report.getTimeWorked());
        dto.setTimeOver(report.getTimeOver());
        dto.setRateBusiness(report.getRateBusiness());
        dto.setRateStudy(report.getRateStudy());
        dto.setTrendBusiness(report.getTrendBusiness());
        dto.setContentMember(report.getContentMember());
        dto.setContentCustomer(report.getContentCustomer());
        dto.setContentProblem(report.getContentProblem());
        dto.setEvaluationBusiness(report.getEvaluationBusiness());
        dto.setEvaluationStudy(report.getEvaluationStudy());
        dto.setGoalBusiness(report.getGoalBusiness());
        dto.setGoalStudy(report.getGoalStudy());
        dto.setContentCompany(report.getContentCompany());
        dto.setContentOthers(report.getContentOthers());
        dto.setCompleteFlg(report.isCompleteFlg());
        dto.setComment(report.getComment());
        dto.setReportDeadline(report.getReportDeadline());
        dto.setApprovalFlg(report.getApprovalFlg());
        dto.setEmployeeCode(report.getEmployee().getCode());
        dto.setEmployeeName(report.getEmployee().getLastName() + " " + report.getEmployee().getFirstName());
        dto.setDepartmentName(report.getEmployee().getDepartment().getName());
        return dto;
    }

    /** DTO → Entity 変換（更新や保存時に利用）*/
    public static Report toEntity(ReportDto dto, Employee employee) {
        Report report = new Report();
        report.setId(dto.getId());
        report.setReportMonth(dto.getReportMonth());
        report.setSubmittedAt(dto.getSubmittedAt());
        report.setUpdatedAt(dto.getUpdatedAt());
        report.setContentBusiness(dto.getContentBusiness());
        report.setTimeWorked(dto.getTimeWorked());
        report.setTimeOver(dto.getTimeOver());
        report.setRateBusiness(dto.getRateBusiness());
        report.setRateStudy(dto.getRateStudy());
        report.setTrendBusiness(dto.getTrendBusiness());
        report.setContentMember(dto.getContentMember());
        report.setContentCustomer(dto.getContentCustomer());
        report.setContentProblem(dto.getContentProblem());
        report.setEvaluationBusiness(dto.getEvaluationBusiness());
        report.setEvaluationStudy(dto.getEvaluationStudy());
        report.setGoalBusiness(dto.getGoalBusiness());
        report.setGoalStudy(dto.getGoalStudy());
        report.setContentCompany(dto.getContentCompany());
        report.setContentOthers(dto.getContentOthers());
        report.setCompleteFlg(dto.isCompleteFlg());
        report.setComment(dto.getComment());
        report.setReportDeadline(dto.getReportDeadline());
        report.setApprovalFlg(dto.getApprovalFlg());
        report.setEmployee(employee);
        return report;
    }
}

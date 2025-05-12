package com.kagoshima.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import lombok.Data;

@Data
public class ReportDto {
    private Integer id;
    private YearMonth reportMonth;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private String contentBusiness;
    private Integer timeWorked;
    private Integer timeOver;
    private Integer rateBusiness;
    private Integer rateStudy;
    private Integer trendBusiness;
    private String contentMember;
    private String contentCustomer;
    private String contentProblem;
    private String evaluationBusiness;
    private String evaluationStudy;
    private String goalBusiness;
    private String goalStudy;
    private String contentCompany;
    private String contentOthers;
    private boolean completeFlg;
    private String comment;
    private LocalDate reportDeadline;
    private Boolean approvalFlg;
    private String employeeCode;
    private String employeeName; // オプション：フロントで表示用に便利
    private String departmentName; // オプション：フロントで表示用に便利
}


package com.kagoshima.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 月報年月
    @Column(nullable = false)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth reportMonth;

    // 削除フラグ(論理削除を行うため)
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    // 提出日時
    @Column
    private LocalDateTime submittedAt;

    // 更新日時
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 業務内容
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 1500)
    private String contentBusiness;

    // 勤務時間（分単位）
    @Column(nullable = false)
    @Max(12000)
    @Min(0)
    @NotNull
    private Integer timeWorked;

    // 残業時間（分単位）
    @Column(nullable = false)
    @Max(12000)
    @Min(0)
    @NotNull
    private Integer timeOver;

    // 業務目標達成率
    @Column(nullable = false)
    @Max(999)
    @Min(0)
    @NotNull
    private Integer rateBusiness;

    // 学習目標達成率
    @Column(nullable = false)
    @Max(999)
    @Min(0)
    @NotNull
    private Integer rateStudy;

    // 業務量推移
    @Column(nullable = false)
    @Max(999)
    @Min(0)
    @NotNull
    private Integer trendBusiness;

    // その他メンバー関連内容
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 800)
    private String contentMember;

    // お客様情報
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 800)
    private String contentCustomer;

    // 問題点内容
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 800)
    private String contentProblem;

    // 自己評価（先月業務目標）
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String evaluationBusiness;

    // 自己評価（先月学習目標）
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String evaluationStudy;

    // 今月業務目標
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String goalBusiness;

    // 今月学習目標
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String goalStudy;

    // 会社関係
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String contentCompany;

    // その他
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 500)
    private String contentOthers;

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 完了フラグ
    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean completeFlg;

    // コメント
    @Column(columnDefinition = "LONGTEXT")
    @Length(max = 800)
    private String comment;

    // 提出期日
    @Column(nullable = false)
    private LocalDate reportDeadline;

    // 承認フラグ
    @Column(columnDefinition = "TINYINT")
    private Boolean approvalFlg;
    
    // 提出期日
    @ManyToOne(optional = true) // null許可
    @JoinColumn(name = "due_date_id", referencedColumnName = "id", nullable = true)
    private ReportDueDate dueDate;

    // コンストラクタで初期値設定
    public Report() {
        reportMonth = YearMonth.now();
        updatedAt = LocalDateTime.now();
        contentBusiness = "";
        timeWorked = 0;
        timeOver = 0;
        rateBusiness = 0;
        rateStudy = 0;
        trendBusiness = 0;
        contentMember = "";
        contentCustomer = "";
        contentProblem = "<問題点>\r\n<解決策>\r\n<ヒヤリハット>";
        evaluationBusiness = "";
        evaluationStudy = "";
        goalBusiness = "";
        goalStudy = "";
        contentCompany = "";
        contentOthers = "";
        completeFlg = false;
        comment = "";
    }

}
package com.kagoshima;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Report;
import com.kagoshima.entity.ReportDueDate;
import com.kagoshima.service.EmailService;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.ReportDueDateService;
import com.kagoshima.service.ReportService;

@Component
public class ScheduledTasks {

	private final EmailService emailService;
	private final ReportService reportService;
	private final EmployeeService employeeService;
	private final MessageSource messageSource;
	private final ReportDueDateService reportDueDateService;

	@Autowired
	public ScheduledTasks(EmailService emailService, ReportService reportService, EmployeeService employeeService,
			MessageSource messageSource, ReportDueDateService reportDueDateService) {
		this.emailService = emailService;
		this.reportService = reportService;
		this.employeeService = employeeService;
		this.messageSource = messageSource;
		this.reportDueDateService = reportDueDateService;
	}

	@Scheduled(cron = "0 * * * * ?")
	public void performTask() {
		if (!isDueDateExactlyNow()) {
			return;
		}

		ArrayList<Employee> toList = new ArrayList<>();

		List<Employee> generalEmployees = employeeService.findByRole(Employee.Role.GENERAL);
		for (Employee employee : generalEmployees) {
			List<Report> reports = reportService.findByEmployee(employee);

			if (reports == null || !isReportCreated(reports)) {
				toList.add(employee);
			}
		}
		sendEmail(toList);
	}

	private void sendEmail(ArrayList<Employee> toList) {
		String month = String.valueOf(YearMonth.now().getMonthValue());
		String subject = messageSource.getMessage("subject.remind", new Object[] { month }, Locale.JAPAN);

		for (Employee emp : toList) {
			String body = messageSource.getMessage("body.remind", new Object[] { emp.getFullName(), month },
					Locale.JAPAN);
			emailService.sendSimpleEmail(emp.getEmail(), subject, body);
		}
	}

	// 当月分の報告書が作成済みか
	private boolean isReportCreated(List<Report> reports) {
		YearMonth currentMonth = YearMonth.now();
		return reports.stream().anyMatch(report -> currentMonth.equals(report.getReportMonth()));
	}

	private boolean isDueDateExactlyNow() {
		LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // 秒とナノ秒を切り捨てる
		YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonth());
		ReportDueDate reportDueDate = reportDueDateService.findByYearMonth(yearMonth).orElse(null);

		if (reportDueDate != null) {
			LocalDateTime dueDate = reportDueDate.getDueDate().withSecond(0).withNano(0);
			return now.equals(dueDate);
		} else {
			LocalDate endOfMonth = yearMonth.atEndOfMonth();
			LocalDateTime defaultDueDate = endOfMonth.atTime(18, 0);
			return now.equals(defaultDueDate);
		}
	}

}

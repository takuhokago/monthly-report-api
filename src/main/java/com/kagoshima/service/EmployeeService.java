package com.kagoshima.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kagoshima.api.dto.EmployeeRequest;
import com.kagoshima.api.mapper.EmployeeMapper;
import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
import com.kagoshima.entity.Employee.Role;
import com.kagoshima.entity.Report;
import com.kagoshima.repository.DepartmentRepository;
import com.kagoshima.repository.EmployeeRepository;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final PasswordEncoder passwordEncoder;
	private final ReportService reportService;
	private final DepartmentRepository departmentRepository;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder,
			ReportService reportService, DepartmentRepository departmentRepository) {
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
		this.reportService = reportService;
		this.departmentRepository = departmentRepository;
	}

	// 従業員更新
	@Transactional
	public ErrorKinds update(Employee employee) {
		Employee existing = findByCode(employee.getCode());
		if (existing == null) {
			return ErrorKinds.NOT_FOUND_ERROR;
		}

		// パスワードチェック
		if (employee.getPassword() == null || employee.getPassword().isBlank()) {
			employee.setPassword(existing.getPassword());
		} else {
			ErrorKinds result = employeePasswordCheck(employee);
			if (ErrorKinds.CHECK_OK != result) {
				return result;
			}

			// ✅ ここで初めてパスワードをエンコードして上書き
			employee.setPassword(passwordEncoder.encode(employee.getPassword()));
		}

		// roleを再設定
		if (employee.getRole() == null) {
			employee.setRole(existing.getRole());
		}

		// reportListを再設定
		List<Report> reports = reportService.findByEmployee(employee);
		if (reports != null) {
			employee.setReportList(reports);
		}

		// createdAt再設定
		employee.setCreatedAt(existing.getCreatedAt());
		employee.setUpdatedAt(LocalDateTime.now());

		employeeRepository.save(employee);
		return ErrorKinds.SUCCESS;
	}

	// 従業員削除
	@Transactional
	public ErrorKinds delete(String code, UserDetail userDetail) {

		// 自分を削除しようとした場合はエラーメッセージを表示
		if (code.equals(userDetail.getEmployee().getCode())) {
			return ErrorKinds.LOGINCHECK_ERROR;
		}

		Employee employee = findByCode(code);
		if (employee == null) {
			throw new IllegalArgumentException("指定された従業員が存在しません: code=" + code);
		}
		LocalDateTime now = LocalDateTime.now();
		employee.setUpdatedAt(now);
		employee.setDeleteFlg(true);

		// 削除対象の従業員（employee）に紐づいている、日報のリスト（reportList）を取得
		List<Report> reportList = reportService.findByEmployee(employee);

		// 日報のリスト（reportList）を拡張for文を使って繰り返し
		for (Report report : reportList) {
			// 日報（report）のIDを指定して、日報情報を削除
			reportService.delete(report.getId().toString());
		}

		return ErrorKinds.SUCCESS;
	}

	// 従業員一覧表示処理
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	// 社員codeで検索
	public Employee findByCode(String code) {
		// findByIdで検索
		Optional<Employee> option = employeeRepository.findById(code);
		// 取得できなかった場合はnullを返す
		Employee employee = option.orElse(null);
		return employee;
	}

	// Departmentで検索
	public List<Employee> findByDepartment(Department department) {
		return employeeRepository.findByDepartment(department);
	}

	// Roleで検索
	public List<Employee> findByRole(Role role) {
		return employeeRepository.findByRole(role);
	}

	// 従業員パスワードチェック
	private ErrorKinds employeePasswordCheck(Employee employee) {
		if (isHalfSizeCheckError(employee)) {
			return ErrorKinds.HALFSIZE_ERROR;
		}

		if (isOutOfRangePassword(employee)) {
			return ErrorKinds.RANGECHECK_ERROR;
		}

		// パスワードの加工（エンコード）はここで行わない
		return ErrorKinds.CHECK_OK;
	}

	// 従業員パスワードの半角英数字チェック処理
	private boolean isHalfSizeCheckError(Employee employee) {

		// 半角英数字チェック
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher matcher = pattern.matcher(employee.getPassword());
		return !matcher.matches();
	}

	// 従業員パスワードの8文字～16文字チェック処理
	public boolean isOutOfRangePassword(Employee employee) {

		// 桁数チェック
		int passwordLength = employee.getPassword().length();
		return passwordLength < 8 || 16 < passwordLength;
	}

	@Transactional
	public Employee save(EmployeeRequest request) {
		// リクエストDTO → エンティティ変換
		Employee employee = EmployeeMapper.toEntity(request);

		// 所属設定
		Department department = departmentRepository.findByName(request.departmentName());
		if (department == null) {
			throw new IllegalArgumentException("指定された所属が存在しません: " + request.departmentName());
		}
		employee.setDepartment(department);

		// フラグとタイムスタンプ
		employee.setDeleteFlg(false);
		LocalDateTime now = LocalDateTime.now();
		employee.setCreatedAt(now);
		employee.setUpdatedAt(now);

		// パスワードのハッシュ化
		String rawPassword = request.password(); // パスワードがリクエストに含まれている前提
		if (rawPassword == null || rawPassword.isBlank()) {
			throw new IllegalArgumentException("パスワードは必須です。");
		}
		String encodedPassword = passwordEncoder.encode(rawPassword);
		employee.setPassword(encodedPassword);

		return employeeRepository.save(employee);
	}

}

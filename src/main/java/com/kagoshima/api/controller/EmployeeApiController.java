package com.kagoshima.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.EmployeeRequest;
import com.kagoshima.api.dto.EmployeeResponse;
import com.kagoshima.api.mapper.EmployeeMapper;
import com.kagoshima.constants.ErrorKinds;
import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
import com.kagoshima.repository.DepartmentRepository;
import com.kagoshima.service.EmployeeService;
import com.kagoshima.service.UserDetail;

@RestController
@RequestMapping("/api/employees")
public class EmployeeApiController {

	private final EmployeeService employeeService;
	private final DepartmentRepository departmentRepository;

	@Autowired
	public EmployeeApiController(EmployeeService employeeService, DepartmentRepository departmentRepository) {
		this.employeeService = employeeService;
		this.departmentRepository = departmentRepository;
	}

	// 一覧取得
	@GetMapping
	public EmployeeListResponse getAllEmployees() {
		List<Employee> employees = employeeService.findAll();
		List<EmployeeResponse> dtoList = employees.stream().map(EmployeeMapper::toResponse).toList();
		return new EmployeeListResponse(dtoList.size(), dtoList);
	}

	@GetMapping("/{code}")
	public ResponseEntity<?> getEmployee(@PathVariable String code) {
		Employee employee = employeeService.findByCode(code);
		if (employee == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
	}

	// 新規作成
	@PostMapping
	public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequest request) {
		try {
			Employee saved = employeeService.save(request); // ← request をそのまま渡す
			return ResponseEntity.ok(EmployeeMapper.toResponse(saved));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	// 更新
	@PutMapping("/{code}")
	public ResponseEntity<?> updateEmployee(@PathVariable String code, @RequestBody EmployeeRequest request) {
		if (!code.equals(request.code())) {
			return ResponseEntity.badRequest().body(Map.of("message", "URLのcodeとボディのcodeが一致しません"));
		}

		Employee employee = EmployeeMapper.toEntity(request);
		Department department = departmentRepository.findByName(request.departmentName());
		if (department == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "所属が存在しません: " + request.departmentName()));
		}
		employee.setDepartment(department);

		ErrorKinds result = employeeService.update(employee);
		if (result == ErrorKinds.SUCCESS) {
			return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
		} else {
			return ResponseEntity.badRequest().body(Map.of("message", "更新エラー: " + result.name()));
		}
	}

	// 削除
	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteEmployee(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail) {
		ErrorKinds result = employeeService.delete(code, userDetail);

		if (result == ErrorKinds.SUCCESS) {
			return ResponseEntity.ok(Map.of("message", "削除に成功しました"));
		} else if (result == ErrorKinds.LOGINCHECK_ERROR) {
			return ResponseEntity.badRequest().body(Map.of("message", "自分自身は削除できません"));
		} else {
			return ResponseEntity.badRequest().body(Map.of("message", "削除エラー: " + result.name()));
		}
	}

	// ログイン中の従業員情報を取得
	@GetMapping("/me")
	public ResponseEntity<EmployeeResponse> getCurrentEmployee(@AuthenticationPrincipal UserDetail userDetail) {
		Employee employee = userDetail.getEmployee();
		return ResponseEntity.ok(EmployeeMapper.toResponse(employee));
	}

	// レスポンスラッパー
	public record EmployeeListResponse(int listSize, List<EmployeeResponse> employeeList) {
	}
}

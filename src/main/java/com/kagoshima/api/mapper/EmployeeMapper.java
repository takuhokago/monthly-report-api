package com.kagoshima.api.mapper;

import java.util.List;

import com.kagoshima.api.dto.EmployeeRequest;
import com.kagoshima.api.dto.EmployeeResponse;
import com.kagoshima.entity.Employee;

public class EmployeeMapper {

	// Entity → Response DTO
	public static EmployeeResponse toResponse(Employee employee) {
		return new EmployeeResponse(employee.getCode(), employee.getLastName(), employee.getFirstName(),
				employee.getFullName(), employee.getEmail(), employee.getRole().getValue(), // "管理者" または "一般"
				employee.getDepartment() != null ? employee.getDepartment().getName() : null);
	}

	// Entity List → Response DTO List
	public static List<EmployeeResponse> toResponseList(List<Employee> employees) {
		return employees.stream().map(EmployeeMapper::toResponse).toList();
	}

	// Request DTO → Entity
	public static Employee toEntity(EmployeeRequest dto) {
		Employee employee = new Employee();

		employee.setCode(dto.code());
		employee.setLastName(dto.lastName());
		employee.setFirstName(dto.firstName());
		employee.setEmail(dto.email());
		employee.setPassword(dto.password());

		try {
			employee.setRole(Employee.Role.valueOf(dto.role())); // ← 修正ポイント
		} catch (IllegalArgumentException | NullPointerException e) {
			// 万一不正な値が来たときの保険（エラーを投げてもOK）
			employee.setRole(Employee.Role.GENERAL);
		}

		return employee;
	}

}

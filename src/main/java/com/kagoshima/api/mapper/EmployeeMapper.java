package com.kagoshima.api.mapper;

import java.util.List;

import com.kagoshima.api.dto.EmployeeDto;
import com.kagoshima.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
            employee.getCode(),
            employee.getLastName(),
            employee.getFirstName(),
            employee.getFullName(),
            employee.getEmail(),
            employee.getRole().getValue(), // "管理者" または "一般"
            employee.getDepartment().getName()
        );
    }

    public static List<EmployeeDto> toDtoList(List<Employee> employees) {
        return employees.stream().map(EmployeeMapper::toDto).toList();
    }
    
    public static Employee toEntity(EmployeeDto dto) {
        Employee employee = new Employee();

        employee.setCode(dto.code());
        employee.setLastName(dto.lastName());
        employee.setFirstName(dto.firstName());
        employee.setEmail(dto.email());

        // 文字列から列挙型へ変換
        if ("管理者".equals(dto.role())) {
            employee.setRole(Employee.Role.ADMIN);
        } else {
            employee.setRole(Employee.Role.GENERAL);
        }

        return employee;
    }

}

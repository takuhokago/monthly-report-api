package com.kagoshima.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kagoshima.api.dto.EmployeeDto;
import com.kagoshima.api.mapper.EmployeeMapper;
import com.kagoshima.entity.Employee;
import com.kagoshima.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeApiController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 従業員一覧取得（GET /api/employees）
    @GetMapping
    public EmployeeListResponse getAllEmployees() {
        List<Employee> employees = employeeService.findAll();
        List<EmployeeDto> dtoList = EmployeeMapper.toDtoList(employees);
        return new EmployeeListResponse(dtoList.size(), dtoList);
    }

    // 従業員詳細取得（GET /api/employees/{code}）
    @GetMapping("/{code}")
    public EmployeeDto getEmployee(@PathVariable String code) {
        Employee employee = employeeService.findByCode(code);
        return EmployeeMapper.toDto(employee);
    }
    
    // 従業員新規作成
    @PostMapping
    public EmployeeDto createEmployee(@RequestBody EmployeeDto dto) {
        Employee saved = employeeService.save(dto);
        return EmployeeMapper.toDto(saved);
    }


    public record EmployeeListResponse(int listSize, List<EmployeeDto> employeeList) {}
}

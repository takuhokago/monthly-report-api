package com.kagoshima.api.dto;

public record EmployeeDto(
    String code,
    String lastName,
    String firstName,
    String fullName,
    String email,
    String role,
    String departmentName
) {}

package com.kagoshima.api.dto;

public record EmployeeResponse(
	    String code,
	    String lastName,
	    String firstName,
	    String fullName,
	    String email,
	    String role,
	    String departmentName
	) {}

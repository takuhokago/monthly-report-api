package com.kagoshima.api.dto;

public record EmployeeRequest(
	    String code,
	    String lastName,
	    String firstName,
	    String email,
	    String role,
	    String departmentName,
	    String password // ← リクエストにだけ含める
	) {}

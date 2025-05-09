package com.kagoshima.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kagoshima.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, String> {
}
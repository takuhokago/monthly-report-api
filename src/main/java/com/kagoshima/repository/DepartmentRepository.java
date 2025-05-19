package com.kagoshima.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kagoshima.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, String> {
	// 名前で部署を検索
	Department findByName(String name);

	boolean existsByName(String name);
}
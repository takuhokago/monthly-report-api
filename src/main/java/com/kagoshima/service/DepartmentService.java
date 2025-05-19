package com.kagoshima.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kagoshima.entity.Department;
import com.kagoshima.entity.Employee;
import com.kagoshima.repository.DepartmentRepository;
import com.kagoshima.repository.EmployeeRepository;

@Service
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;

	@Autowired
	public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
		this.departmentRepository = departmentRepository;
		this.employeeRepository = employeeRepository;
	}

	// 所属保存
	@Transactional
	public void save(Department department) {
		// 所属名の重複チェック
		if (departmentRepository.existsByName(department.getName())) {
			throw new IllegalArgumentException("この所属名は既に存在します。");
		}

		departmentRepository.save(department);
	}

	// 所属名更新
	@Transactional
	public void update(Department department) {
		departmentRepository.save(department);
	}

	// 所属削除
	@Transactional
	public void delete(List<Department> departmentList) {
		for (Department department : departmentList) {
			// 部署に従業員が紐づいているかを確認
			if (employeeRepository.existsByDepartment(department)) {
				throw new IllegalStateException("所属「" + department.getName() + "」には従業員が存在するため削除できません。");
			}
			departmentRepository.delete(department);
		}
	}

	// 削除
	@Transactional
	public void deleteById(String id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("指定された所属が存在しません。"));

		if (employeeRepository.existsByDepartment(department)) {
			throw new IllegalStateException("所属「" + department.getName() + "」には従業員が存在するため削除できません。");
		}

		departmentRepository.delete(department);
	}

	// 所属一覧表示処理
	public List<Department> findAll() {
		return departmentRepository.findAll();
	}

	public boolean existsById(String id) {
		try {
			return departmentRepository.existsById(id);
		} catch (NumberFormatException e) {
			return false; // 不正なIDは存在しないと判断
		}
	}

}

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

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // 所属保存
    @Transactional
    public void save(Department department) {
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
    	for(Department department : departmentList) {
    		departmentRepository.delete(department);
    	}
    }
    
    // 削除
    @Transactional
    public void deleteById(String id) {
    	departmentRepository.deleteById(id);
    }
    
    

    // 所属一覧表示処理
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    


}

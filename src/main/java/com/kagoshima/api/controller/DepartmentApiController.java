package com.kagoshima.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.DepartmentDto;
import com.kagoshima.api.mapper.DepartmentMapper;
import com.kagoshima.entity.Department;
import com.kagoshima.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentApiController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    // 所属一覧取得
    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<Department> departments = departmentService.findAll();
        List<DepartmentDto> dtoList = departmentMapper.toDtoList(departments);
        return ResponseEntity.ok(dtoList);
    }

    // 所属追加
    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto dto) {
        Department entity = departmentMapper.toEntity(dto);
        departmentService.save(entity);
        return ResponseEntity.ok(departmentMapper.toDto(entity));
    }

    // 所属削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        if (departmentService.existsById(String.valueOf(id))) {
        	departmentService.deleteById(String.valueOf(id));
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}

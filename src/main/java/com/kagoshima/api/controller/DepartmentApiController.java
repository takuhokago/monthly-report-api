package com.kagoshima.api.controller;

import java.util.List;
import java.util.Map;

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
	public ResponseEntity<?> createDepartment(@RequestBody DepartmentDto dto) {
		try {
			Department entity = departmentMapper.toEntity(dto);
			departmentService.save(entity); // ← 名前重複チェックを含む
			return ResponseEntity.ok(departmentMapper.toDto(entity));
		} catch (IllegalArgumentException e) {
			// バリデーション失敗時は 400 Bad Request として返す
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDepartment(@PathVariable Integer id) {
		String departmentId = String.valueOf(id);

		if (!departmentService.existsById(departmentId)) {
			return ResponseEntity.notFound().build(); // 404
		}

		try {
			departmentService.deleteById(departmentId);
			return ResponseEntity.noContent().build(); // 204
		} catch (IllegalStateException e) {
			return ResponseEntity.status(409).body(Map.of("message", e.getMessage())); // 409 Conflict
		}
	}

}

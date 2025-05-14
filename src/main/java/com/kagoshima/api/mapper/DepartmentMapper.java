package com.kagoshima.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kagoshima.api.dto.DepartmentDto;
import com.kagoshima.entity.Department;

@Component
public class DepartmentMapper {

    // Entity → DTO
    public DepartmentDto toDto(Department entity) {
        if (entity == null) {
            return null;
        }

        DepartmentDto dto = new DepartmentDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    // DTO → Entity
    public Department toEntity(DepartmentDto dto) {
        if (dto == null) {
            return null;
        }

        Department entity = new Department();
        entity.setId(dto.getId()); // 更新時などに必要
        entity.setName(dto.getName());
        return entity;
    }

    // Entityリスト → DTOリスト
    public List<DepartmentDto> toDtoList(List<Department> entities) {
        return entities.stream()
                       .map(this::toDto)
                       .collect(Collectors.toList());
    }

    // DTOリスト → Entityリスト
    public List<Department> toEntityList(List<DepartmentDto> dtos) {
        return dtos.stream()
                   .map(this::toEntity)
                   .collect(Collectors.toList());
    }
}

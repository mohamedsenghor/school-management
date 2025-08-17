package com.bassine.metier.mapper;

import com.bassine.metier.dto.SectorsDto;
import com.bassine.metier.entity.SectorsEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SectorsMapper {

    // Entity vers DTO
    public static SectorsDto toDto(SectorsEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SectorsDto dto = new SectorsDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Conversion des classes si elles sont chargées
        if (entity.getClasses() != null && !entity.getClasses().isEmpty()) {
            dto.setClasses(ClassesMapper.listToDto(entity.getClasses()));
        }
        
        return dto;
    }

    // DTO vers Entity
    public static SectorsEntity toEntity(SectorsDto dto) {
        if (dto == null) {
            return null;
        }
        
        SectorsEntity entity = new SectorsEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        return entity;
    }

    // Liste Entity vers Liste DTO
    public static List<SectorsDto> listToDto(List<SectorsEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(SectorsMapper::toDto)
                .collect(Collectors.toList());
    }

    // Liste DTO vers Liste Entity
    public static List<SectorsEntity> listToEntity(List<SectorsDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(SectorsMapper::toEntity)
                .collect(Collectors.toList());
    }

    // Entity vers DTO simple (sans classes)
    public static SectorsDto toDtoSimple(SectorsEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SectorsDto dto = new SectorsDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}

package com.bassine.metier.mapper;

import com.bassine.metier.dto.ClassesDto;
import com.bassine.metier.entity.ClassesEntity;
import com.bassine.metier.entity.SectorsEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ClassesMapper {

    // Entity vers DTO
    public static ClassesDto toDto(ClassesEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ClassesDto dto = new ClassesDto();
        dto.setId(entity.getId());
        dto.setClassName(entity.getClassName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Informations du secteur
        if (entity.getSector() != null) {
            dto.setSectorId(entity.getSector().getId());
            dto.setSectorName(entity.getSector().getName());
        }
        
        return dto;
    }

    // DTO vers Entity
    public static ClassesEntity toEntity(ClassesDto dto) {
        if (dto == null) {
            return null;
        }
        
        ClassesEntity entity = new ClassesEntity();
        entity.setId(dto.getId());
        entity.setClassName(dto.getClassName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        // Le secteur sera assigné au niveau du service
        if (dto.getSectorId() != null) {
            SectorsEntity sector = new SectorsEntity();
            sector.setId(dto.getSectorId());
            entity.setSector(sector);
        }
        
        return entity;
    }

    // Liste Entity vers Liste DTO
    public static List<ClassesDto> listToDto(List<ClassesEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(ClassesMapper::toDto)
                .collect(Collectors.toList());
    }

    // Liste DTO vers Liste Entity
    public static List<ClassesEntity> listToEntity(List<ClassesDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(ClassesMapper::toEntity)
                .collect(Collectors.toList());
    }

    // Entity vers DTO simple (sans secteur détaillé)
    public static ClassesDto toDtoSimple(ClassesEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ClassesDto dto = new ClassesDto();
        dto.setId(entity.getId());
        dto.setClassName(entity.getClassName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}

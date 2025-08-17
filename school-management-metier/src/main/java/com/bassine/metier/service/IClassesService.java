package com.bassine.metier.service;

import com.bassine.metier.dto.ClassesDto;

import java.util.List;
import java.util.Optional;

public interface IClassesService {
    
    // CRUD operations
    boolean save(ClassesDto classDto);
    
    boolean update(ClassesDto classDto);
    
    boolean delete(Long id);
    
    ClassesDto get(Long id);
    
    List<ClassesDto> getAll();
    
    // Business methods
    List<ClassesDto> getClassesBySector(Long sectorId);
    
    List<ClassesDto> searchByClassName(String className);
    
    Optional<ClassesDto> findByClassNameAndSector(String className, Long sectorId);
    
    List<ClassesDto> getClassesBySectorName(String sectorName);
    
    boolean existsByClassNameAndSector(String className, Long sectorId);
    
    // Validation methods
    boolean isValidClassName(String className);
    
    boolean isValidDescription(String description);
}

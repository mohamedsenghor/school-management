package com.bassine.metier.service;

import com.bassine.metier.dto.SectorsDto;

import java.util.List;
import java.util.Optional;

public interface ISectorsService {
    
    // CRUD operations
    boolean save(SectorsDto sectorDto);
    
    boolean update(SectorsDto sectorDto);
    
    boolean delete(Long id);
    
    SectorsDto get(Long id);
    
    List<SectorsDto> getAll();
    
    // Business methods
    Optional<SectorsDto> findByName(String name);
    
    List<SectorsDto> searchByName(String name);
    
    SectorsDto getSectorWithClasses(Long id);
    
    boolean existsByName(String name);
    
    long countClasses(Long sectorId);
    
    // Validation methods
    boolean isValidSectorName(String name);
    
    boolean canDeleteSector(Long id);
}

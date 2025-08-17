package com.bassine.metier.dao;

import com.bassine.metier.entity.ClassesEntity;

import java.util.List;
import java.util.Optional;

public interface IClassesDao extends Repository<ClassesEntity> {
    
    List<ClassesEntity> findBySectorId(Long sectorId);
    
    List<ClassesEntity> searchByClassNameContaining(String className);
    
    Optional<ClassesEntity> findByClassNameAndSectorId(String className, Long sectorId);
    
    List<ClassesEntity> findBySectorName(String sectorName);
    
    boolean existsByClassNameAndSectorId(String className, Long sectorId);
}

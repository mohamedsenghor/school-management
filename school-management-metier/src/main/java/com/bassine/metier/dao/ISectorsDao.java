package com.bassine.metier.dao;

import com.bassine.metier.entity.SectorsEntity;

import java.util.List;
import java.util.Optional;

public interface ISectorsDao extends Repository<SectorsEntity> {
    
    Optional<SectorsEntity> findByName(String name);
    
    List<SectorsEntity> searchByNameContaining(String name);
    
    SectorsEntity findByIdWithClasses(Long id);
    
    boolean existsByName(String name);
    
    long countClasses(Long sectorId);
}

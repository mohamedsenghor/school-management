package com.bassine.metier.service;

import com.bassine.metier.dao.IClassesDao;
import com.bassine.metier.dao.ClassesDao;
import com.bassine.metier.dao.ISectorsDao;
import com.bassine.metier.dao.SectorsDao;
import com.bassine.metier.dto.ClassesDto;
import com.bassine.metier.entity.ClassesEntity;
import com.bassine.metier.entity.SectorsEntity;
import com.bassine.metier.mapper.ClassesMapper;

import java.util.List;
import java.util.Optional;

public class ClassesService implements IClassesService {

    private final IClassesDao classesDao = new ClassesDao();
    private final ISectorsDao sectorsDao = new SectorsDao();

    @Override
    public boolean save(ClassesDto classDto) {
        if (classDto == null || 
            !isValidClassName(classDto.getClassName()) || 
            classDto.getSectorId() == null) {
            return false;
        }
        
        // Vérifier que le secteur existe
        Optional<SectorsEntity> sector = sectorsDao.findById(classDto.getSectorId());
        if (sector.isEmpty()) {
            return false;
        }
        
        // Vérifier qu'une classe avec le même nom n'existe pas déjà dans ce secteur
        if (existsByClassNameAndSector(classDto.getClassName(), classDto.getSectorId())) {
            return false;
        }
        
        ClassesEntity entity = ClassesMapper.toEntity(classDto);
        entity.setSector(sector.get());
        
        return classesDao.save(entity);
    }

    @Override
    public boolean update(ClassesDto classDto) {
        if (classDto == null || 
            classDto.getId() == null || 
            !isValidClassName(classDto.getClassName()) || 
            classDto.getSectorId() == null) {
            return false;
        }
        
        // Vérifier que l'entité existe
        Optional<ClassesEntity> existingEntity = classesDao.findById(classDto.getId());
        if (existingEntity.isEmpty()) {
            return false;
        }
        
        // Vérifier que le secteur existe
        Optional<SectorsEntity> sector = sectorsDao.findById(classDto.getSectorId());
        if (sector.isEmpty()) {
            return false;
        }
        
        // Vérifier qu'une autre classe avec le même nom n'existe pas déjà dans ce secteur
        Optional<ClassesEntity> classWithSameName = classesDao.findByClassNameAndSectorId(
            classDto.getClassName(), classDto.getSectorId());
        if (classWithSameName.isPresent() && !classWithSameName.get().getId().equals(classDto.getId())) {
            return false;
        }
        
        ClassesEntity entity = ClassesMapper.toEntity(classDto);
        entity.setSector(sector.get());
        
        return classesDao.update(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        return classesDao.delete(id, new ClassesEntity());
    }

    @Override
    public ClassesDto get(Long id) {
        if (id == null) {
            return null;
        }
        
        Optional<ClassesEntity> entity = classesDao.findById(id);
        return entity.map(ClassesMapper::toDto).orElse(null);
    }

    @Override
    public List<ClassesDto> getAll() {
        List<ClassesEntity> entities = classesDao.list(new ClassesEntity());
        return ClassesMapper.listToDto(entities);
    }

    @Override
    public List<ClassesDto> getClassesBySector(Long sectorId) {
        if (sectorId == null) {
            return List.of();
        }
        
        List<ClassesEntity> entities = classesDao.findBySectorId(sectorId);
        return ClassesMapper.listToDto(entities);
    }

    @Override
    public List<ClassesDto> searchByClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            return getAll();
        }
        
        List<ClassesEntity> entities = classesDao.searchByClassNameContaining(className.trim());
        return ClassesMapper.listToDto(entities);
    }

    @Override
    public Optional<ClassesDto> findByClassNameAndSector(String className, Long sectorId) {
        if (className == null || className.trim().isEmpty() || sectorId == null) {
            return Optional.empty();
        }
        
        Optional<ClassesEntity> entity = classesDao.findByClassNameAndSectorId(className.trim(), sectorId);
        return entity.map(ClassesMapper::toDto);
    }

    @Override
    public List<ClassesDto> getClassesBySectorName(String sectorName) {
        if (sectorName == null || sectorName.trim().isEmpty()) {
            return List.of();
        }
        
        List<ClassesEntity> entities = classesDao.findBySectorName(sectorName.trim());
        return ClassesMapper.listToDto(entities);
    }

    @Override
    public boolean existsByClassNameAndSector(String className, Long sectorId) {
        if (className == null || className.trim().isEmpty() || sectorId == null) {
            return false;
        }
        
        return classesDao.existsByClassNameAndSectorId(className.trim(), sectorId);
    }

    @Override
    public boolean isValidClassName(String className) {
        return className != null && 
               !className.trim().isEmpty() && 
               className.trim().length() >= 2 && 
               className.trim().length() <= 255;
    }

    @Override
    public boolean isValidDescription(String description) {
        // La description peut être vide ou null
        return description == null || description.length() <= 1000;
    }
}

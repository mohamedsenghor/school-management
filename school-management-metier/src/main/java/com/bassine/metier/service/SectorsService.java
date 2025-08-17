package com.bassine.metier.service;

import com.bassine.metier.dao.ISectorsDao;
import com.bassine.metier.dao.SectorsDao;
import com.bassine.metier.dto.SectorsDto;
import com.bassine.metier.entity.SectorsEntity;
import com.bassine.metier.mapper.SectorsMapper;

import java.util.List;
import java.util.Optional;

public class SectorsService implements ISectorsService {

    private final ISectorsDao sectorsDao = new SectorsDao();

    @Override
    public boolean save(SectorsDto sectorDto) {
        if (sectorDto == null || !isValidSectorName(sectorDto.getName())) {
            return false;
        }
        
        // Vérifier que le nom n'existe pas déjà
        if (existsByName(sectorDto.getName())) {
            return false;
        }
        
        SectorsEntity entity = SectorsMapper.toEntity(sectorDto);
        return sectorsDao.save(entity);
    }

    @Override
    public boolean update(SectorsDto sectorDto) {
        if (sectorDto == null || sectorDto.getId() == null || !isValidSectorName(sectorDto.getName())) {
            return false;
        }
        
        // Vérifier que l'entité existe
        Optional<SectorsEntity> existingEntity = sectorsDao.findById(sectorDto.getId());
        if (existingEntity.isEmpty()) {
            return false;
        }
        
        // Vérifier que le nouveau nom n'est pas déjà utilisé par un autre secteur
        Optional<SectorsEntity> sectorWithSameName = sectorsDao.findByName(sectorDto.getName());
        if (sectorWithSameName.isPresent() && !sectorWithSameName.get().getId().equals(sectorDto.getId())) {
            return false;
        }
        
        SectorsEntity entity = SectorsMapper.toEntity(sectorDto);
        return sectorsDao.update(entity);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        // Vérifier si on peut supprimer le secteur
        if (!canDeleteSector(id)) {
            return false;
        }
        
        return sectorsDao.delete(id, new SectorsEntity());
    }

    @Override
    public SectorsDto get(Long id) {
        if (id == null) {
            return null;
        }
        
        Optional<SectorsEntity> entity = sectorsDao.findById(id);
        return entity.map(SectorsMapper::toDto).orElse(null);
    }

    @Override
    public List<SectorsDto> getAll() {
        List<SectorsEntity> entities = sectorsDao.list(new SectorsEntity());
        return SectorsMapper.listToDto(entities);
    }

    @Override
    public Optional<SectorsDto> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Optional<SectorsEntity> entity = sectorsDao.findByName(name.trim());
        return entity.map(SectorsMapper::toDto);
    }

    @Override
    public List<SectorsDto> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAll();
        }
        
        List<SectorsEntity> entities = sectorsDao.searchByNameContaining(name.trim());
        return SectorsMapper.listToDto(entities);
    }

    @Override
    public SectorsDto getSectorWithClasses(Long id) {
        if (id == null) {
            return null;
        }
        
        SectorsEntity entity = sectorsDao.findByIdWithClasses(id);
        return SectorsMapper.toDto(entity);
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return sectorsDao.existsByName(name.trim());
    }

    @Override
    public long countClasses(Long sectorId) {
        if (sectorId == null) {
            return 0;
        }
        
        return sectorsDao.countClasses(sectorId);
    }

    @Override
    public boolean isValidSectorName(String name) {
        return name != null && 
               !name.trim().isEmpty() && 
               name.trim().length() >= 2 && 
               name.trim().length() <= 255;
    }

    @Override
    public boolean canDeleteSector(Long id) {
        if (id == null) {
            return false;
        }
        
        // On peut supprimer un secteur même s'il a des classes (CASCADE)
        // Mais on peut ajouter des règles business ici si nécessaire
        return sectorsDao.findById(id).isPresent();
    }
}

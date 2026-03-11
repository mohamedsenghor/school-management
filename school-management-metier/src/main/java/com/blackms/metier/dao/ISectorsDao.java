package dev.blackms.metier.dao;

import dev.blackms.metier.entity.SectorsEntity;

import java.util.List;
import java.util.Optional;

public interface ISectorsDao extends Repository<SectorsEntity> {

    Optional<SectorsEntity> findByName(String name);

    List<SectorsEntity> searchByNameContaining(String name);

    SectorsEntity findByIdWithClasses(Long id);

    boolean existsByName(String name);

    long countClasses(Long sectorId);
}

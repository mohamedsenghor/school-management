package com.bassine.metier.dao;

import com.bassine.metier.config.HibernateUtil;
import com.bassine.metier.entity.SectorsEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class SectorsDao extends RepositoryImpl<SectorsEntity> implements ISectorsDao {

    @Override
    public Optional<SectorsEntity> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SectorsEntity sector = session.get(SectorsEntity.class, id);
            return Optional.ofNullable(sector);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<SectorsEntity> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SectorsEntity> query = session.createQuery(
                "FROM SectorsEntity s WHERE s.name = :name", SectorsEntity.class);
            query.setParameter("name", name);
            SectorsEntity sector = query.uniqueResult();
            return Optional.ofNullable(sector);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<SectorsEntity> searchByNameContaining(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SectorsEntity> query = session.createQuery(
                "FROM SectorsEntity s WHERE s.name LIKE :name ORDER BY s.name", SectorsEntity.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public SectorsEntity findByIdWithClasses(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<SectorsEntity> query = session.createQuery(
                "SELECT s FROM SectorsEntity s LEFT JOIN FETCH s.classes WHERE s.id = :id", 
                SectorsEntity.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean existsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(s) FROM SectorsEntity s WHERE s.name = :name", Long.class);
            query.setParameter("name", name);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long countClasses(Long sectorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM ClassesEntity c WHERE c.sector.id = :sectorId", Long.class);
            query.setParameter("sectorId", sectorId);
            Long count = query.uniqueResult();
            return count != null ? count : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}

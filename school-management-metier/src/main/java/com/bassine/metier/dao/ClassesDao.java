package com.bassine.metier.dao;

import com.bassine.metier.config.HibernateUtil;
import com.bassine.metier.entity.ClassesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ClassesDao extends RepositoryImpl<ClassesEntity> implements IClassesDao {

    @Override
    public Optional<ClassesEntity> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ClassesEntity classe = session.get(ClassesEntity.class, id);
            return Optional.ofNullable(classe);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<ClassesEntity> findBySectorId(Long sectorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ClassesEntity> query = session.createQuery(
                "FROM ClassesEntity c WHERE c.sector.id = :sectorId ORDER BY c.className", 
                ClassesEntity.class);
            query.setParameter("sectorId", sectorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<ClassesEntity> searchByClassNameContaining(String className) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ClassesEntity> query = session.createQuery(
                "FROM ClassesEntity c WHERE c.className LIKE :className ORDER BY c.className", 
                ClassesEntity.class);
            query.setParameter("className", "%" + className + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<ClassesEntity> findByClassNameAndSectorId(String className, Long sectorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ClassesEntity> query = session.createQuery(
                "FROM ClassesEntity c WHERE c.className = :className AND c.sector.id = :sectorId", 
                ClassesEntity.class);
            query.setParameter("className", className);
            query.setParameter("sectorId", sectorId);
            ClassesEntity classe = query.uniqueResult();
            return Optional.ofNullable(classe);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<ClassesEntity> findBySectorName(String sectorName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ClassesEntity> query = session.createQuery(
                "FROM ClassesEntity c WHERE c.sector.name = :sectorName ORDER BY c.className", 
                ClassesEntity.class);
            query.setParameter("sectorName", sectorName);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean existsByClassNameAndSectorId(String className, Long sectorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM ClassesEntity c WHERE c.className = :className AND c.sector.id = :sectorId", 
                Long.class);
            query.setParameter("className", className);
            query.setParameter("sectorId", sectorId);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

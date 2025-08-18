# GUIDE D'IMPLÉMENTATION ÉTAPE PAR ÉTAPE

## Projet SOAP Maven Multi-Modules - Secteurs et Classes

---

## ÉTAPE 1 : CONFIGURATION DU PROJET

### 1.1 Création du projet parent

```bash
# Créer le répertoire principal
mkdir school-management
cd school-management

# Créer le pom.xml parent
```

**pom.xml** (racine)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bassine</groupId>
    <artifactId>school-management</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>metier</module>
        <module>soap</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.9.2</junit.version>
        <hibernate.version>5.4.10.Final</hibernate.version>
        <mysql.version>8.0.13</mysql.version>
    </properties>

</project>
```

### 1.2 Structure des répertoires

```bash
mkdir -p metier/src/main/java/dev/black/metier/{entity,dto,dao,service,mapper,config,exception}
mkdir -p metier/src/main/resources
mkdir -p metier/src/test/java

mkdir -p soap/src/main/java/dev/black/soap/webservice/service
mkdir -p soap/src/main/webapp/WEB-INF
mkdir -p soap/src/test/java

mkdir docker
```

---

## ÉTAPE 2 : CONFIGURATION DE LA BASE DE DONNÉES

### 2.1 Docker Compose

#### **docker/docker-compose.yml**

```yaml
version: '3'

services:
  mysql-sectors:
    image: mysql:8.0
    container_name: mysql-sectors-classes
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: sectors_classes_db
      MYSQL_USER: user
      MYSQL_PASSWORD: passer123@
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - backend

  phpmyadmin:
    container_name: phpmyadmin-sectors
    image: phpmyadmin/phpmyadmin:latest
    ports:
      - "8085:80"
    environment:
      MYSQL_ROOT_PASSWORD: root
      PMA_HOST: mysql-sectors
      PMA_USER: user
      PMA_PASSWORD: passer123@
    depends_on:
      - mysql-sectors
    networks:
      - backend

volumes:
  mysql_data:

networks:
  backend:
```

### 2.2 Script SQL de création

#### **scripts/create_database.sql**

```sql
CREATE DATABASE IF NOT EXISTS sectors_classes_db;
USE sectors_classes_db;

CREATE TABLE sectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    description TEXT,
    sector_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sector_id) REFERENCES sectors(id) ON DELETE CASCADE
);

-- Index pour optimiser les performances
CREATE INDEX idx_classes_sector_id ON classes(sector_id);
CREATE INDEX idx_sectors_name ON sectors(name);
CREATE INDEX idx_classes_name ON classes(class_name);

-- Données de test
INSERT INTO sectors (name) VALUES 
('Technologie'),
('Finance'),
('Santé'),
('Éducation'),
('Commerce');

INSERT INTO classes (class_name, description, sector_id) VALUES 
('Développement Web', 'Formation en développement web full-stack', 1),
('Intelligence Artificielle', 'Cours d\'IA et Machine Learning', 1),
('Cybersécurité', 'Formation en sécurité informatique', 1),
('Comptabilité Générale', 'Cours de comptabilité de base', 2),
('Analyse Financière', 'Formation en analyse et gestion financière', 2),
('Trading', 'Cours de trading et marchés financiers', 2),
('Médecine Générale', 'Formation médicale générale', 3),
('Pharmacie', 'Cours de pharmacologie', 3),
('Pédagogie', 'Formation des enseignants', 4),
('Psychologie Éducative', 'Psychologie appliquée à l\'éducation', 4);
```

---

## ÉTAPE 3 : MODULE MÉTIER

### 3.1 Configuration POM du module métier

#### **metier/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.bassine</groupId>
        <artifactId>school-management</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>metier</artifactId>

    <dependencies>
        <!-- Hibernate -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        
        <!-- Logger -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.6</version>
        </dependency>
        
        <!-- JUnit -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 3.2 Configuration Hibernate

#### **metier/src/main/resources/database.properties**

```properties
db.username=user
db.password=passer123@
db.urlDev=jdbc:mysql://localhost:3306/sectors_classes_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
```

#### **metier/src/main/java/dev/black/metier/config/HibernateUtil.java**

```java
package com.bassine.metier.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import entity.com.bassine.metier.SectorsEntity;
import entity.com.bassine.metier.ClassesEntity;

import java.util.Properties;

public class HibernateUtil {
    
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                
                Properties settings = new Properties();
                PropertiesReader propertiesReader = new PropertiesReader();
                
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, propertiesReader.getUrl());
                settings.put(Environment.USER, propertiesReader.getUsername());
                settings.put(Environment.PASS, propertiesReader.getPassword());
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                
                configuration.setProperties(settings);
                
                // Ajouter les entités
                configuration.addAnnotatedClass(SectorsEntity.class);
                configuration.addAnnotatedClass(ClassesEntity.class);
                
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
                    
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
```

### 3.3 Entités JPA

#### **metier/src/main/java/dev/black/metier/entity/SectorsEntity.java**

```java
package com.bassine.metier.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sectors")
public class SectorsEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClassesEntity> classes = new ArrayList<>();

    // Constructeurs
    public SectorsEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public SectorsEntity(String name) {
        this();
        this.name = name;
    }

    public SectorsEntity(Long id, String name) {
        this(name);
        this.id = id;
    }

    // Méthodes utilitaires pour la relation
    public void addClasse(ClassesEntity classe) {
        classes.add(classe);
        classe.setSector(this);
    }

    public void removeClasse(ClassesEntity classe) {
        classes.remove(classe);
        classe.setSector(null);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ClassesEntity> getClasses() { return classes; }
    public void setClasses(List<ClassesEntity> classes) { this.classes = classes; }

    @Override
    public String toString() {
        return "SectorsEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

#### **metier/src/main/java/dev/black/metier/entity/ClassesEntity.java**

```java
package com.bassine.metier.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
public class ClassesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, length = 255)
    private String className;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private SectorsEntity sector;

    // Constructeurs
    public ClassesEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ClassesEntity(String className, String description) {
        this();
        this.className = className;
        this.description = description;
    }

    public ClassesEntity(Long id, String className, String description, SectorsEntity sector) {
        this(className, description);
        this.id = id;
        this.sector = sector;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public SectorsEntity getSector() { return sector; }
    public void setSector(SectorsEntity sector) { this.sector = sector; }

    @Override
    public String toString() {
        return "ClassesEntity{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

### 3.4 DTOs

#### **metier/src/main/java/dev/black/metier/dto/SectorsDto.java**

```java
package com.bassine.metier.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class SectorsDto implements Serializable {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ClassesDto> classes;

    // Constructeurs
    public SectorsDto() {}

    public SectorsDto(String name) {
        this.name = name;
    }

    public SectorsDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ClassesDto> getClasses() { return classes; }
    public void setClasses(List<ClassesDto> classes) { this.classes = classes; }

    @Override
    public String toString() {
        return "SectorsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
```

#### **metier/src/main/java/dev/black/metier/dto/ClassesDto.java**

```java
package com.bassine.metier.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClassesDto implements Serializable {

    private Long id;
    private String className;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long sectorId;
    private String sectorName;

    // Constructeurs
    public ClassesDto() {}

    public ClassesDto(String className, String description) {
        this.className = className;
        this.description = description;
    }

    public ClassesDto(Long id, String className, String description, Long sectorId) {
        this.id = id;
        this.className = className;
        this.description = description;
        this.sectorId = sectorId;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    @Override
    public String toString() {
        return "ClassesDto{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", description='" + description + '\'' +
                ", sectorId=" + sectorId +
                ", sectorName='" + sectorName + '\'' +
                '}';
    }
}
```

---

## ÉTAPE 4 : COUCHE DAO (DATA ACCESS OBJECTS)

### 4.1 Interface Repository générique

**metier/src/main/java/dev/black/metier/dao/Repository.java**
```java
package com.bassine.metier.dao;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    
    boolean save(T t);
    
    boolean update(T t);
    
    boolean delete(Long id, T t);
    
    T get(Long id, T t);
    
    List<T> list(T t);
    
    Optional<T> findById(Long id);
}
```

### 4.2 Implémentation Repository générique

**metier/src/main/java/dev/black/metier/dao/RepositoryImpl.java**
```java
package com.bassine.metier.dao;

import config.com.bassine.metier.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class RepositoryImpl<T> implements Repository<T> {

    @Override
    public boolean save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id, T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entityToDelete = (T) session.get(entity.getClass(), id);
            if (entityToDelete != null) {
                session.delete(entityToDelete);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public T get(Long id, T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return (T) session.get(entity.getClass(), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> list(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entity.getClass().getSimpleName(), (Class<T>) entity.getClass());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        // Cette méthode sera implémentée dans les DAOs spécialisés
        return Optional.empty();
    }
}
```

### 4.3 Interface DAO spécialisée pour Sectors

**metier/src/main/java/dev/black/metier/dao/ISectorsDao.java**
```java
package com.bassine.metier.dao;

import entity.com.bassine.metier.SectorsEntity;

import java.util.List;
import java.util.Optional;

public interface ISectorsDao extends Repository<SectorsEntity> {
    
    Optional<SectorsEntity> findByName(String name);
    
    List<SectorsEntity> searchByNameContaining(String name);
    
    SectorsEntity findByIdWithClasses(Long id);
    
    boolean existsByName(String name);
    
    long countClasses(Long sectorId);
}
```

### 4.4 Implémentation DAO pour Sectors

**metier/src/main/java/dev/black/metier/dao/SectorsDao.java**
```java
package com.bassine.metier.dao;

import config.com.bassine.metier.HibernateUtil;
import entity.com.bassine.metier.SectorsEntity;
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
```

### 4.5 Interface DAO spécialisée pour Classes

**metier/src/main/java/dev/black/metier/dao/IClassesDao.java**
```java
package com.bassine.metier.dao;

import entity.com.bassine.metier.ClassesEntity;

import java.util.List;
import java.util.Optional;

public interface IClassesDao extends Repository<ClassesEntity> {
    
    List<ClassesEntity> findBySectorId(Long sectorId);
    
    List<ClassesEntity> searchByClassNameContaining(String className);
    
    Optional<ClassesEntity> findByClassNameAndSectorId(String className, Long sectorId);
    
    List<ClassesEntity> findBySectorName(String sectorName);
    
    boolean existsByClassNameAndSectorId(String className, Long sectorId);
}
```

### 4.6 Implémentation DAO pour Classes

**metier/src/main/java/dev/black/metier/dao/ClassesDao.java**
```java
package com.bassine.metier.dao;

import config.com.bassine.metier.HibernateUtil;
import entity.com.bassine.metier.ClassesEntity;
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
```

---

## ÉTAPE 5 : MAPPERS (CONVERSION ENTITY/DTO)

### 5.1 Mapper pour Sectors

**metier/src/main/java/dev/black/metier/mapper/SectorsMapper.java**
```java
package com.bassine.metier.mapper;

import dto.com.bassine.metier.SectorsDto;
import entity.com.bassine.metier.SectorsEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SectorsMapper {

    // Entity vers DTO
    public static SectorsDto toDto(SectorsEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SectorsDto dto = new SectorsDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Conversion des classes si elles sont chargées
        if (entity.getClasses() != null && !entity.getClasses().isEmpty()) {
            dto.setClasses(ClassesMapper.listToDto(entity.getClasses()));
        }
        
        return dto;
    }

    // DTO vers Entity
    public static SectorsEntity toEntity(SectorsDto dto) {
        if (dto == null) {
            return null;
        }
        
        SectorsEntity entity = new SectorsEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        
        return entity;
    }

    // Liste Entity vers Liste DTO
    public static List<SectorsDto> listToDto(List<SectorsEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(SectorsMapper::toDto)
                .collect(Collectors.toList());
    }

    // Liste DTO vers Liste Entity
    public static List<SectorsEntity> listToEntity(List<SectorsDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(SectorsMapper::toEntity)
                .collect(Collectors.toList());
    }

    // Entity vers DTO simple (sans classes)
    public static SectorsDto toDtoSimple(SectorsEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SectorsDto dto = new SectorsDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
}
```

### 5.2 Mapper pour Classes

**metier/src/main/java/dev/black/metier/mapper/ClassesMapper.java**
```java
package com.bassine.metier.mapper;

import dto.com.bassine.metier.ClassesDto;
import entity.com.bassine.metier.ClassesEntity;
import entity.com.bassine.metier.SectorsEntity;

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
```

---

## ÉTAPE 6 : COUCHE SERVICE (LOGIQUE MÉTIER)

### 6.1 Interface Service pour Sectors

**metier/src/main/java/dev/black/metier/service/ISectorsService.java**
```java
package com.bassine.metier.service;

import dto.com.bassine.metier.SectorsDto;

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
```

### 6.2 Implémentation Service pour Sectors

**metier/src/main/java/dev/black/metier/service/SectorsService.java**
```java
package com.bassine.metier.service;

import dao.com.bassine.metier.ISectorsDao;
import dao.com.bassine.metier.SectorsDao;
import dto.com.bassine.metier.SectorsDto;
import entity.com.bassine.metier.SectorsEntity;
import mapper.com.bassine.metier.SectorsMapper;

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
```

### 6.3 Interface Service pour Classes

**metier/src/main/java/dev/black/metier/service/IClassesService.java**
```java
package com.bassine.metier.service;

import dto.com.bassine.metier.ClassesDto;

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
```

### 6.4 Implémentation Service pour Classes

**metier/src/main/java/dev/black/metier/service/ClassesService.java**
```java
package com.bassine.metier.service;

import dao.com.bassine.metier.IClassesDao;
import dao.com.bassine.metier.ClassesDao;
import dao.com.bassine.metier.ISectorsDao;
import dao.com.bassine.metier.SectorsDao;
import dto.com.bassine.metier.ClassesDto;
import entity.com.bassine.metier.ClassesEntity;
import entity.com.bassine.metier.SectorsEntity;
import mapper.com.bassine.metier.ClassesMapper;

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
```

---

## ÉTAPE 7 : MODULE SOAP (WEB SERVICES)

### 7.1 Configuration POM du module SOAP

**soap/pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.bassine</groupId>
        <artifactId>school-management</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>soap</artifactId>
    <packaging>war</packaging>

    <dependencies>
        <!-- Dépendance vers le module métier -->
        <dependency>
            <groupId>com.bassine</groupId>
            <artifactId>metier</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        
        <!-- Jakarta Servlet API -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>5.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- Jakarta JWS API -->
        <dependency>
            <groupId>jakarta.jws</groupId>
            <artifactId>jakarta.jws-api</artifactId>
            <version>3.0.0</version>
        </dependency>

        <!-- JAX-WS Runtime -->
        <dependency>
            <groupId>com.sun.xml.ws</groupId>
            <artifactId>jaxws-rt</artifactId>
            <version>4.0.0</version>
        </dependency>

        <!-- JAX-WS Tools -->
        <dependency>
            <groupId>com.sun.xml.ws</groupId>
            <artifactId>jaxws-tools</artifactId>
            <version>4.0.0</version>
        </dependency>

        <!-- JUnit pour les tests -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.2.3</version>
                <configuration>
                    <webXml>src\main\webapp\WEB-INF\web.xml</webXml>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 7.2 Interface Web Service pour Sectors

**soap/src/main/java/dev/black/soap/webservice/service/SectorsWebService.java**
```java
package com.bassine.soap.webservice.service;

import dto.com.bassine.metier.SectorsDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "SectorsWebService", targetNamespace = "http://bassine.com/school-management")
public interface SectorsWebService {

    @WebMethod(operationName = "getSector")
    SectorsDto getSector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "getAllSectors")
    List<SectorsDto> getAllSectors();

    @WebMethod(operationName = "saveSector")
    SectorsDto saveSector(@WebParam(name = "sector") SectorsDto sectorDto);

    @WebMethod(operationName = "updateSector")
    SectorsDto updateSector(@WebParam(name = "sector") SectorsDto sectorDto);

    @WebMethod(operationName = "deleteSector")
    boolean deleteSector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "searchSectorsByName")
    List<SectorsDto> searchSectorsByName(@WebParam(name = "name") String name);

    @WebMethod(operationName = "getSectorWithClasses")
    SectorsDto getSectorWithClasses(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "existsSectorByName")
    boolean existsSectorByName(@WebParam(name = "name") String name);

    @WebMethod(operationName = "countClassesInSector")
    long countClassesInSector(@WebParam(name = "sectorId") Long sectorId);
}
```

### 7.3 Implémentation Web Service pour Sectors

**soap/src/main/java/dev/black/soap/webservice/service/SectorsWebServiceImpl.java**
```java
package com.bassine.soap.webservice.service;

import dto.com.bassine.metier.SectorsDto;
import service.com.bassine.metier.ISectorsService;
import service.com.bassine.metier.SectorsService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.Optional;

@WebService(
    endpointInterface = "com.bassine.soap.webservice.service.SectorsWebService",
    serviceName = "SectorsWebService",
    portName = "SectorsWebServicePort",
    targetNamespace = "http://bassine.com/school-management"
)
public class SectorsWebServiceImpl implements SectorsWebService {

    private final ISectorsService sectorsService = new SectorsService();

    @Override
    @WebMethod(operationName = "getSector")
    public SectorsDto getSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return null;
            }
            return sectorsService.get(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "getAllSectors")
    public List<SectorsDto> getAllSectors() {
        try {
            return sectorsService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "saveSector")
    public SectorsDto saveSector(@WebParam(name = "sector") SectorsDto sectorDto) {
        try {
            if (sectorDto == null) {
                return null;
            }
            
            boolean saved = sectorsService.save(sectorDto);
            if (saved) {
                // Récupérer le secteur créé par son nom
                Optional<SectorsDto> createdSector = sectorsService.findByName(sectorDto.getName());
                return createdSector.orElse(null);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "updateSector")
    public SectorsDto updateSector(@WebParam(name = "sector") SectorsDto sectorDto) {
        try {
            if (sectorDto == null || sectorDto.getId() == null) {
                return null;
            }
            
            boolean updated = sectorsService.update(sectorDto);
            if (updated) {
                return sectorsService.get(sectorDto.getId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "deleteSector")
    public boolean deleteSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return false;
            }
            return sectorsService.delete(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "searchSectorsByName")
    public List<SectorsDto> searchSectorsByName(@WebParam(name = "name") String name) {
        try {
            return sectorsService.searchByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "getSectorWithClasses")
    public SectorsDto getSectorWithClasses(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return null;
            }
            return sectorsService.getSectorWithClasses(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "existsSectorByName")
    public boolean existsSectorByName(@WebParam(name = "name") String name) {
        try {
            return sectorsService.existsByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "countClassesInSector")
    public long countClassesInSector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            return sectorsService.countClasses(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
```

### 7.4 Interface Web Service pour Classes

**soap/src/main/java/dev/black/soap/webservice/service/ClassesWebService.java**
```java
package com.bassine.soap.webservice.service;

import dto.com.bassine.metier.ClassesDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

@WebService(name = "ClassesWebService", targetNamespace = "http://bassine.com/school-management")
public interface ClassesWebService {

    @WebMethod(operationName = "getClass")
    ClassesDto getClass(@WebParam(name = "classId") Long classId);

    @WebMethod(operationName = "getAllClasses")
    List<ClassesDto> getAllClasses();

    @WebMethod(operationName = "saveClass")
    ClassesDto saveClass(@WebParam(name = "classe") ClassesDto classDto);

    @WebMethod(operationName = "updateClass")
    ClassesDto updateClass(@WebParam(name = "classe") ClassesDto classDto);

    @WebMethod(operationName = "deleteClass")
    boolean deleteClass(@WebParam(name = "classId") Long classId);

    @WebMethod(operationName = "getClassesBySector")
    List<ClassesDto> getClassesBySector(@WebParam(name = "sectorId") Long sectorId);

    @WebMethod(operationName = "searchClassesByName")
    List<ClassesDto> searchClassesByName(@WebParam(name = "className") String className);

    @WebMethod(operationName = "getClassesBySectorName")
    List<ClassesDto> getClassesBySectorName(@WebParam(name = "sectorName") String sectorName);

    @WebMethod(operationName = "existsClassInSector")
    boolean existsClassInSector(@WebParam(name = "className") String className, 
                               @WebParam(name = "sectorId") Long sectorId);
}
```

### 7.5 Implémentation Web Service pour Classes

**soap/src/main/java/dev/black/soap/webservice/service/ClassesWebServiceImpl.java**
```java
package com.bassine.soap.webservice.service;

import dto.com.bassine.metier.ClassesDto;
import service.com.bassine.metier.IClassesService;
import service.com.bassine.metier.ClassesService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;
import java.util.Optional;

@WebService(
    endpointInterface = "com.bassine.soap.webservice.service.ClassesWebService",
    serviceName = "ClassesWebService",
    portName = "ClassesWebServicePort",
    targetNamespace = "http://bassine.com/school-management"
)
public class ClassesWebServiceImpl implements ClassesWebService {

    private final IClassesService classesService = new ClassesService();

    @Override
    @WebMethod(operationName = "getClass")
    public ClassesDto getClass(@WebParam(name = "classId") Long classId) {
        try {
            if (classId == null) {
                return null;
            }
            return classesService.get(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "getAllClasses")
    public List<ClassesDto> getAllClasses() {
        try {
            return classesService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "saveClass")
    public ClassesDto saveClass(@WebParam(name = "classe") ClassesDto classDto) {
        try {
            if (classDto == null) {
                return null;
            }
            
            boolean saved = classesService.save(classDto);
            if (saved) {
                // Récupérer la classe créée
                Optional<ClassesDto> createdClass = classesService.findByClassNameAndSector(
                    classDto.getClassName(), classDto.getSectorId());
                return createdClass.orElse(null);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "updateClass")
    public ClassesDto updateClass(@WebParam(name = "classe") ClassesDto classDto) {
        try {
            if (classDto == null || classDto.getId() == null) {
                return null;
            }
            
            boolean updated = classesService.update(classDto);
            if (updated) {
                return classesService.get(classDto.getId());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @WebMethod(operationName = "deleteClass")
    public boolean deleteClass(@WebParam(name = "classId") Long classId) {
        try {
            if (classId == null) {
                return false;
            }
            return classesService.delete(classId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @WebMethod(operationName = "getClassesBySector")
    public List<ClassesDto> getClassesBySector(@WebParam(name = "sectorId") Long sectorId) {
        try {
            if (sectorId == null) {
                return List.of();
            }
            return classesService.getClassesBySector(sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "searchClassesByName")
    public List<ClassesDto> searchClassesByName(@WebParam(name = "className") String className) {
        try {
            return classesService.searchByClassName(className);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "getClassesBySectorName")
    public List<ClassesDto> getClassesBySectorName(@WebParam(name = "sectorName") String sectorName) {
        try {
            return classesService.getClassesBySectorName(sectorName);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    @WebMethod(operationName = "existsClassInSector")
    public boolean existsClassInSector(@WebParam(name = "className") String className, 
                                     @WebParam(name = "sectorId") Long sectorId) {
        try {
            return classesService.existsByClassNameAndSector(className, sectorId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
```

---

## ÉTAPE 8 : CONFIGURATION WEB

### 8.1 Configuration Web.xml

**soap/src/main/webapp/WEB-INF/web.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
         https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd"
         version="5.0">

    <display-name>Sectors Classes SOAP Web Services</display-name>
    
    <!-- Listener pour JAX-WS -->
    <listener>
        <listener-class>
            com.sun.xml.ws.transport.http.servlet.WSServletContextListener
        </listener-class>
    </listener>

    <!-- Servlet pour Sectors Web Service -->
    <servlet>
        <servlet-name>SectorsWebService</servlet-name>
        <servlet-class>
            com.sun.xml.ws.transport.http.servlet.WSServlet
        </servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>SectorsWebService</servlet-name>
        <url-pattern>/sectorsWebService</url-pattern>
    </servlet-mapping>

    <!-- Servlet pour Classes Web Service -->
    <servlet>
        <servlet-name>ClassesWebService</servlet-name>
        <servlet-class>
            com.sun.xml.ws.transport.http.servlet.WSServlet
        </servlet-class>
        <load-on-startup>2</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>ClassesWebService</servlet-name>
        <url-pattern>/classesWebService</url-pattern>
    </servlet-mapping>

    <!-- Page d'accueil -->
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

</web-app>
```

### 8.2 Configuration JAX-WS

**soap/src/main/webapp/WEB-INF/sun-jaxws.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<endpoints xmlns="http://java.sun.com/xml/ns/jax-ws/ri/runtime" version="2.0">
    
    <!-- Configuration pour Sectors Web Service -->
    <endpoint
        name="SectorsWebService"
        implementation="com.bassine.soap.webservice.service.SectorsWebServiceImpl"
        url-pattern="/sectorsWebService" />

    <!-- Configuration pour Classes Web Service -->
    <endpoint
        name="ClassesWebService"
        implementation="com.bassine.soap.webservice.service.ClassesWebServiceImpl"
        url-pattern="/classesWebService" />

</endpoints>
```

### 8.3 Page d'accueil

**soap/src/main/webapp/index.jsp**
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sectors & Classes SOAP Web Services</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .service { background: #f5f5f5; padding: 20px; margin: 20px 0; border-radius: 5px; }
        .service h3 { color: #333; margin-top: 0; }
        .endpoint { color: #007acc; text-decoration: none; }
        .endpoint:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>🌐 Sectors & Classes SOAP Web Services</h1>
    <p>Bienvenue sur l'interface des services web SOAP pour la gestion des secteurs et classes.</p>

    <div class="service">
        <h3>📊 Sectors Web Service</h3>
        <p><strong>Endpoint:</strong> <a href="sectorsWebService" class="endpoint">sectorsWebService</a></p>
        <p><strong>WSDL:</strong> <a href="sectorsWebService?wsdl" class="endpoint">sectorsWebService?wsdl</a></p>
        <p>Gestion des secteurs d'activité (CRUD complet, recherche, statistiques)</p>
    </div>

    <div class="service">
        <h3>📚 Classes Web Service</h3>
        <p><strong>Endpoint:</strong> <a href="classesWebService" class="endpoint">classesWebService</a></p>
        <p><strong>WSDL:</strong> <a href="classesWebService?wsdl" class="endpoint">classesWebService?wsdl</a></p>
        <p>Gestion des classes par secteur (CRUD complet, filtrage, recherche)</p>
    </div>

    <hr>
    <p><small>Powered by JAX-WS & Jakarta EE | Version 1.0</small></p>
</body>
</html>
```

---

## ÉTAPE 9 : TESTS DES WEB SERVICES

### 9.1 Exemples de requêtes SOAP pour Sectors

#### Récupérer tous les secteurs
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:getAllSectors/>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Créer un nouveau secteur
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:saveSector>
         <sector>
            <name>Agriculture</name>
         </sector>
      </web:saveSector>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Récupérer un secteur avec ses classes
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:getSectorWithClasses>
         <sectorId>1</sectorId>
      </web:getSectorWithClasses>
   </soapenv:Body>
</soapenv:Envelope>
```

### 9.2 Exemples de requêtes SOAP pour Classes

#### Récupérer les classes d'un secteur
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:getClassesBySector>
         <sectorId>1</sectorId>
      </web:getClassesBySector>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Créer une nouvelle classe
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:saveClass>
         <classe>
            <className>Data Science</className>
            <description>Formation en science des données et analytics</description>
            <sectorId>1</sectorId>
         </classe>
      </web:saveClass>
   </soapenv:Body>
</soapenv:Envelope>
```

#### Rechercher des classes par nom
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:web="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <web:searchClassesByName>
         <className>Développement</className>
      </web:searchClassesByName>
   </soapenv:Body>
</soapenv:Envelope>
```

---

## PROCHAINES ÉTAPES

Maintenant que nous avons implémenté le module SOAP complet, la suite comprendra :

1. **Tests unitaires** - JUnit 5 pour toutes les couches
2. **Tests d'intégration SOAP** - Tests des web services
3. **Gestion d'erreurs** - Exceptions SOAP personnalisées
4. **Déploiement Tomcat** - Configuration et déploiement
5. **Documentation** - Guide complet d'utilisation

Le projet est maintenant fonctionnel avec des web services SOAP exposant toute la logique métier !

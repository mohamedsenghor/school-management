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

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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

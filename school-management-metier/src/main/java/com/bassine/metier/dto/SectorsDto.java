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

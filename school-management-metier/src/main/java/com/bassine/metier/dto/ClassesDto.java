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

CREATE DATABASE IF NOT EXISTS classes_management_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE classes_management_db;

CREATE TABLE sectors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    description TEXT,
    sector_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sector_id) REFERENCES sectors(id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    description TEXT,
    sector_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sector_id) REFERENCES sectors(id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

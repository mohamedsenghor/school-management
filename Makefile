# Makefile pour School Management Application - Docker

.PHONY: help build up down start stop restart logs clean rebuild test backup restore

# Variables
COMPOSE_FILE = docker-compose.yml
PROJECT_NAME = school-management

# Couleurs pour l'affichage
GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
RESET  := \033[0m

## help: Afficher ce message d'aide
help:
	@echo "$(GREEN)School Management Application - Commandes Docker$(RESET)"
	@echo ""
	@echo "$(YELLOW)Commandes disponibles:$(RESET)"
	@echo "  make build      - Construire les images Docker"
	@echo "  make up         - Démarrer tous les services"
	@echo "  make down       - Arrêter et supprimer les conteneurs"
	@echo "  make start      - Démarrer les services existants"
	@echo "  make stop       - Arrêter les services"
	@echo "  make restart    - Redémarrer tous les services"
	@echo "  make logs       - Afficher les logs en temps réel"
	@echo "  make clean      - Nettoyage complet (conteneurs + volumes)"
	@echo "  make rebuild    - Reconstruire et redémarrer"
	@echo "  make ps         - Voir l'état des conteneurs"
	@echo "  make test       - Tester les endpoints"
	@echo "  make backup     - Sauvegarder la base de données"
	@echo "  make restore    - Restaurer la base de données"
	@echo ""

## build: Construire les images Docker sans démarrer
build:
	@echo "$(GREEN)Construction des images Docker...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) build

## up: Construire et démarrer tous les services en arrière-plan
up:
	@echo "$(GREEN)Démarrage de tous les services...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) up -d --build
	@echo "$(GREEN)✓ Services démarrés!$(RESET)"
	@echo ""
	@echo "$(YELLOW)Accès aux services:$(RESET)"
	@echo "  Frontend:     http://localhost"
	@echo "  Backend SOAP: http://localhost:8080/school-management-soap/"
	@echo "  PHPMyAdmin:   http://localhost:8085"
	@echo ""
	@echo "$(YELLOW)Voir les logs:$(RESET) make logs"

## down: Arrêter et supprimer tous les conteneurs
down:
	@echo "$(YELLOW)Arrêt des services...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) down
	@echo "$(GREEN)✓ Services arrêtés$(RESET)"

## start: Démarrer les services existants
start:
	@echo "$(GREEN)Démarrage des services...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) start

## stop: Arrêter les services sans les supprimer
stop:
	@echo "$(YELLOW)Arrêt des services...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) stop

## restart: Redémarrer tous les services
restart:
	@echo "$(YELLOW)Redémarrage des services...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) restart
	@echo "$(GREEN)✓ Services redémarrés$(RESET)"

## logs: Afficher les logs en temps réel
logs:
	docker-compose -f $(COMPOSE_FILE) logs -f

## logs-backend: Afficher les logs du backend uniquement
logs-backend:
	docker-compose -f $(COMPOSE_FILE) logs -f backend

## logs-frontend: Afficher les logs du frontend uniquement
logs-frontend:
	docker-compose -f $(COMPOSE_FILE) logs -f frontend

## logs-db: Afficher les logs de la base de données
logs-db:
	docker-compose -f $(COMPOSE_FILE) logs -f mysql-db

## ps: Voir l'état de tous les conteneurs
ps:
	@docker-compose -f $(COMPOSE_FILE) ps

## clean: Nettoyage complet (conteneurs, volumes, réseaux)
clean:
	@echo "$(RED)⚠️  Nettoyage complet (suppression des données!)$(RESET)"
	@echo "Appuyez sur Ctrl+C pour annuler..."
	@sleep 3
	docker-compose -f $(COMPOSE_FILE) down -v --remove-orphans
	@echo "$(GREEN)✓ Nettoyage terminé$(RESET)"

## rebuild: Reconstruire complètement et redémarrer
rebuild:
	@echo "$(YELLOW)Reconstruction complète...$(RESET)"
	docker-compose -f $(COMPOSE_FILE) down
	docker-compose -f $(COMPOSE_FILE) build --no-cache
	docker-compose -f $(COMPOSE_FILE) up -d
	@echo "$(GREEN)✓ Reconstruction terminée$(RESET)"

## shell-backend: Accéder au shell du conteneur backend
shell-backend:
	docker-compose -f $(COMPOSE_FILE) exec backend bash

## shell-frontend: Accéder au shell du conteneur frontend
shell-frontend:
	docker-compose -f $(COMPOSE_FILE) exec frontend sh

## shell-db: Accéder au shell MySQL
shell-db:
	docker-compose -f $(COMPOSE_FILE) exec mysql-db mysql -u user -ppasser123@ classes_management_db

## test: Tester les endpoints de l'application
test:
	@echo "$(YELLOW)Test des endpoints...$(RESET)"
	@echo ""
	@echo "Frontend:"
	@curl -s -o /dev/null -w "  Status: %{http_code}\n" http://localhost || echo "  $(RED)❌ Frontend non accessible$(RESET)"
	@echo ""
	@echo "Backend SOAP:"
	@curl -s -o /dev/null -w "  Status: %{http_code}\n" http://localhost:8080/school-management-soap/ || echo "  $(RED)❌ Backend non accessible$(RESET)"
	@echo ""
	@echo "PHPMyAdmin:"
	@curl -s -o /dev/null -w "  Status: %{http_code}\n" http://localhost:8085 || echo "  $(RED)❌ PHPMyAdmin non accessible$(RESET)"
	@echo ""

## backup: Sauvegarder la base de données
backup:
	@echo "$(GREEN)Sauvegarde de la base de données...$(RESET)"
	@mkdir -p ./backups
	@docker-compose -f $(COMPOSE_FILE) exec -T mysql-db mysqldump -u user -ppasser123@ classes_management_db > ./backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "$(GREEN)✓ Sauvegarde créée dans ./backups/$(RESET)"

## restore: Restaurer la base de données (fichier: BACKUP_FILE=path/to/backup.sql)
restore:
	@if [ -z "$(BACKUP_FILE)" ]; then \
		echo "$(RED)❌ Erreur: Spécifiez le fichier avec BACKUP_FILE=path/to/backup.sql$(RESET)"; \
		exit 1; \
	fi
	@echo "$(YELLOW)Restauration de la base de données...$(RESET)"
	@docker-compose -f $(COMPOSE_FILE) exec -T mysql-db mysql -u user -ppasser123@ classes_management_db < $(BACKUP_FILE)
	@echo "$(GREEN)✓ Base de données restaurée$(RESET)"

## stats: Afficher les statistiques d'utilisation des conteneurs
stats:
	docker stats --no-stream

## prune: Nettoyer les ressources Docker inutilisées
prune:
	@echo "$(YELLOW)Nettoyage des ressources Docker inutilisées...$(RESET)"
	docker system prune -f
	@echo "$(GREEN)✓ Nettoyage terminé$(RESET)"

## ============================================
## Variables d'Environnement (Gestion centralisée)
## ============================================

## env-setup: Configuration interactive des variables d'environnement
env-setup:
	@bash setup-env.sh

## env-validate: Valider la configuration des variables d'environnement
env-validate:
	@bash validate-env.sh

## env-show: Afficher la configuration actuelle (sans mots de passe)
env-show:
	@echo "$(GREEN)Configuration actuellement chargée depuis .env:$(RESET)"
	@echo ""
	@grep -E '^[A-Z_]+=.*' .env 2>/dev/null | grep -v '^#' | while read line; do \
		key=$$(echo $$line | cut -d'=' -f1); \
		value=$$(echo $$line | cut -d'=' -f2-); \
		if [[ "$$key" == *"PASSWORD"* ]]; then \
			echo "  $$key = ****"; \
		else \
			echo "  $$key = $$value"; \
		fi; \
	done
	@echo ""

## env-guide: Afficher le guide complet des variables d'environnement
env-guide:
	@cat GUIDE-ENV-VARIABLES.md | less

## env-convention: Afficher la convention technique des variables
env-convention:
	@cat ENV-VARIABLE-CONVENTION.md | less

## ============================================
## Groupes de commandes pratiques
## ============================================

## setup: Installation initiale complète (setup + build + up)
setup: env-validate up
	@echo "$(GREEN)✅ Installation initiale terminée!$(RESET)"

## full-restart: Redémarrage complet avec reconstruction
full-restart: down clean rebuild
	@echo "$(GREEN)✅ Redémarrage complet terminé!$(RESET)"


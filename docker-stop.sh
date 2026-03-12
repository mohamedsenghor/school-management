#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║    School Management Application - Docker Stop             ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Vérifier si des conteneurs sont en cours d'exécution
if [ -z "$(docker-compose ps -q)" ]; then
    echo -e "${YELLOW}⚠️  Aucun conteneur en cours d'exécution${NC}"
    exit 0
fi

echo -e "${YELLOW}Conteneurs actifs:${NC}"
docker-compose ps
echo ""

# Menu de choix
echo -e "${BLUE}Que voulez-vous faire?${NC}"
echo -e "  ${GREEN}1${NC} - Arrêter les services (les données)"
echo -e "  ${GREEN}2${NC} - Arrêter et supprimer les conteneurs (les données)"
echo -e "  ${RED}3${NC} - Tout supprimer (conteneurs + volumes = ${RED}PERTE DES DONNÉES${NC})"
echo -e "  ${YELLOW}4${NC} - Annuler"
echo ""
read -p "Votre choix (1-4): " -n 1 -r choice
echo ""
echo ""

case $choice in
    1)
        echo -e "${YELLOW}Arrêt des services...${NC}"
        docker-compose stop
        echo -e "${GREEN}✓ Services arrêtés${NC}"
        echo -e "${BLUE}Pour les redémarrer: ${GREEN}docker-compose start${NC}"
        ;;
    2)
        echo -e "${YELLOW}Arrêt et suppression des conteneurs...${NC}"
        docker-compose down
        echo -e "${GREEN}✓ Conteneurs supprimés${NC}"
        echo -e "${BLUE}Pour les redémarrer: ${GREEN}./docker-start.sh${NC} ou ${GREEN}docker-compose up -d${NC}"
        ;;
    3)
        echo -e "${RED}⚠️  ATTENTION: Cette action va supprimer TOUTES les données!${NC}"
        echo -e "${RED}Les données de la base de données seront perdues.${NC}"
        echo ""
        read -p "Êtes-vous VRAIMENT sûr? (tapez 'oui' pour confirmer): " confirm
        if [ "$confirm" == "oui" ]; then
            echo -e "${RED}Suppression complète...${NC}"
            docker-compose down -v --remove-orphans
            echo -e "${GREEN}✓ Tous les conteneurs et volumes supprimés${NC}"
        else
            echo -e "${YELLOW}Annulé${NC}"
        fi
        ;;
    4|*)
        echo -e "${YELLOW}Annulé${NC}"
        exit 0
        ;;
esac

echo ""
echo -e "${GREEN}Terminé! 👍${NC}"

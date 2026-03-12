#!/bin/bash

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║    School Management Application - Docker Setup            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Vérifier que Docker est installé
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker n'est pas installé!${NC}"
    echo -e "${YELLOW}Installez Docker depuis: https://docs.docker.com/get-docker/${NC}"
    exit 1
fi

# Vérifier que Docker Compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose n'est pas installé!${NC}"
    echo -e "${YELLOW}Installez Docker Compose depuis: https://docs.docker.com/compose/install/${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker est installé${NC}"
echo -e "${GREEN}✓ Docker Compose est installé${NC}"
echo ""

# Vérifier si le fichier .env existe
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  Fichier .env non trouvé${NC}"
    echo -e "${YELLOW}Création du fichier .env avec les valeurs par défaut...${NC}"
    cp .env.example .env
    echo -e "${GREEN}✓ Fichier .env créé${NC}"
    echo ""
fi

# Charger les variables si le script de validation n'existe pas
source .env
echo -e "${BLUE}Configuration:${NC}"
echo -e "  Base de données: ${GREEN}${MYSQL_DATABASE}${NC}"
echo -e "  Utilisateur DB:  ${GREEN}${MYSQL_USER}${NC}"
echo ""

echo -e "${YELLOW}Ce processus va:${NC}"
echo -e "  1. Construire les images Docker pour le frontend et le backend"
echo -e "  2. Démarrer MySQL, le backend SOAP, le frontend React et PHPMyAdmin"
echo -e "  3. Initialiser la base de données avec les données de test"
echo ""

docker-compose up -d

echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✓ Installation terminée avec succès!${NC}"
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo ""

# Vérifier l'état des services
echo -e "${BLUE}État des services:${NC}"
docker-compose ps
echo ""

# Afficher les URLs d'accès
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                  Accès aux services                        ║${NC}"
echo -e "${BLUE}╠════════════════════════════════════════════════════════════╣${NC}"
echo -e "${BLUE}║${NC} ${GREEN}Frontend (React)${NC}                                           ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   → http://localhost                                  ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} ${GREEN}Backend SOAP API${NC}                                           ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   → http://localhost:8080/school-management. -soap/        ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} ${GREEN}WSDL Endpoints${NC}                                             ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   → http://localhost:8080/school-management-soap/.         ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}     classesWebService?wsdl                                 ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   → http://localhost:8080/school-management-soap/          ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}     sectorsWebService?wsdl                                 ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} ${GREEN}PHPMyAdmin${NC}                                                 ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   → http://localhost:8085                                  ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   User: ${YELLOW}user${NC}  Password: ${YELLOW}passer123@${NC}                         ${BLUE}║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Commandes utiles
echo -e "${YELLOW}Commandes utiles:${NC}"
echo -e "  Voir les logs:        ${GREEN}docker-compose logs -f${NC}"
echo -e "  Arrêter les services: ${GREEN}docker-compose stop${NC}"
echo -e "  Redémarrer:           ${GREEN}docker-compose restart${NC}"
echo -e "  Tout supprimer:       ${GREEN}docker-compose down -v${NC}"
echo ""
echo -e "  Ou utilisez:          ${GREEN}make help${NC} (pour voir toutes les commandes)"
echo ""

# Test de connectivité
echo -e "${YELLOW}Test de connectivité...${NC}"
sleep 5

if curl -s -o /dev/null -w "%{http_code}" http://localhost | grep -q "200"; then
    echo -e "${GREEN}✓ Frontend accessible${NC}"
else
    echo -e "${RED}⚠️  Frontend non accessible (peut nécessiter quelques secondes de plus)${NC}"
fi

if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/school-management-soap/ | grep -q "200\|404"; then
    echo -e "${GREEN}✓ Backend accessible${NC}"
else
    echo -e "${RED}⚠️  Backend non accessible (peut nécessiter quelques secondes de plus)${NC}"
fi

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  Configuration des Variables d'Environnement               ║${NC}"
echo -e "${BLUE}╠════════════════════════════════════════════════════════════╣${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} 📋 Toutes les variables d'environnement sont centralisées  ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} dans le fichier ${GREEN}.env${NC}                                       ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} Pour modifier les paramètres :                             ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   1. Éditez le fichier ${GREEN}.env${NC}                                ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}   2. Relancez: ${GREEN}docker-compose up -d${NC}                                ${BLUE}║${NC}"
echo -e "${BLUE}║${NC}                                                            ${BLUE}║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}Bonne utilisation! 🚀${NC}"

#!/bin/bash

FRONTEND_IP="192.168.56.12"
BACKEND_IP="192.168.56.10"
BACKEND_PORT=8080
BACKEND_ENDPOINT="/school-management-soap"
PROJECT_DIR="/home/vagrant/school-management"
FRONTEND_DIR="$PROJECT_DIR/school-management-frontend"
WEB_DIR="/var/www/school-management"
NGINX_CONF="/etc/nginx/sites-available/school-management"

echo "--- Installation de Nginx ---"
sudo apt-get update
sudo apt-get install -y nginx

echo "--- Configuration de l'environnement Node ---"
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

echo "--- Clonage et Préparation du projet ---"
if [ ! -d "$PROJECT_DIR" ]; then
    git clone https://github.com/mohamedsenghor/school-management.git $PROJECT_DIR
fi

cd $FRONTEND_DIR

# .env.example est utilisé comme modèle pour .env.local
if [ ! -f "$FRONTEND_DIR/.env.local" ]; then
    cp $FRONTEND_DIR/.env.example $FRONTEND_DIR/.env.local
fi

# Mise à jour de l'URL Backend dans vite.config.ts
VITE_CONFIG_TS_FILE="$FRONTEND_DIR/vite.config.ts"
if [ -f "$VITE_CONFIG_TS_FILE" ]; then
    echo "Mise à jour de l'URL Backend vers $BACKEND_IP:$BACKEND_PORT..."
    sed -i "s|target: '.*'|target: 'http://$BACKEND_IP:$BACKEND_PORT'|g" $VITE_CONFIG_TS_FILE
fi

echo "--- Compilation (Build) du projet ---"
npm install
npm run build

echo "--- Déploiement des fichiers vers $WEB_DIR ---"
# Créer le dossier de destination et copier les fichiers compilés
sudo mkdir -p $WEB_DIR
sudo cp -r $FRONTEND_DIR/dist/* $WEB_DIR/

# Attribution des permissions à l'utilisateur Nginx
sudo chown -R www-data:www-data $WEB_DIR
sudo chmod -R 755 $WEB_DIR

echo "--- Configuration de Nginx ---"
sudo tee $NGINX_CONF <<EOF
server {
    listen 80;
    server_name $FRONTEND_IP;

    root $WEB_DIR;
    index index.html;

    # Proxy SOAP requests to backend Tomcat
    location $BACKEND_ENDPOINT/ {
        proxy_pass http://$BACKEND_IP:$BACKEND_PORT$BACKEND_ENDPOINT/;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    location / {
        try_files \$uri \$uri/ /index.html;
    }
}
EOF

# Activation et nettoyage
sudo ln -sf $NGINX_CONF /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo systemctl restart nginx

echo "Frontend déployé avec succès sur http://$FRONTEND_IP"

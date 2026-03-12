#!/bin/bash
SCRIPT_DIR="/home/vagrant/scripts"
APP_SERVER_IP="192.168.56.10"

echo "Installation de MySQL Server..."
sudo apt-get update
sudo apt-get install -y mysql-server

# Configurer MySQL pour accepter les connexions distantes (si besoin)
sudo sed -i 's/127.0.0.1/0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf
sudo systemctl restart mysql

# Création de la base de données et de l'utilisateur
# Note : Adaptez 'root' ou créez un utilisateur selon votre database.properties
sudo mysql -e "CREATE DATABASE IF NOT EXISTS classes_management_db;"
sudo mysql -e "CREATE USER IF NOT EXISTS 'user'@'$APP_SERVER_IP' IDENTIFIED BY 'passer123@'; ALTER USER IF EXISTS 'user'@'$APP_SERVER_IP' IDENTIFIED BY 'passer123@';"
sudo mysql -e "GRANT ALL PRIVILEGES ON classes_management_db.* TO 'user'@'$APP_SERVER_IP';"
sudo mysql -e "FLUSH PRIVILEGES;"
sudo mysql < "$SCRIPT_DIR/create_database.sql"

echo "MySQL Server est installé et configuré avec succès!"

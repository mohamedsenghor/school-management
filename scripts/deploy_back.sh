#!/bin/bash
PROJECT_DIR="/home/vagrant/school-management"
TOMCAT_HOME="/opt/tomcat10"

echo "Clonage et Compilation du projet..."
if [ ! -d "$PROJECT_DIR" ]; then
    git clone https://github.com/mohamedsenghor/school-management.git $PROJECT_DIR
fi

# Update the database connection properties in the project
DB_PROPERTIES_FILE="$PROJECT_DIR/school-management-metier/src/main/resources/database.properties"
if [ -f "$DB_PROPERTIES_FILE" ]; then
    echo "Updating database connection properties in $DB_PROPERTIES_FILE..."
    sed -i "s/db.url=jdbc:mysql:\/\/localhost:3306/db.url=jdbc:mysql:\/\/192.168.56.11:3306/" $DB_PROPERTIES_FILE
fi

cd $PROJECT_DIR
# Compilation (ignore les tests pour aller plus vite au premier déploiement)
mvn clean install -DskipTests

echo "Déploiement du fichier WAR dans Tomcat..."
echo "Stopping Tomcat service for deployment..."
sudo systemctl stop tomcat || true
echo "Stopping any legacy Tomcat Java process..."
sudo pkill -f "org.apache.catalina.startup.Bootstrap" || true
sleep 2

# Remove old deployment if exists
sudo rm -rf $TOMCAT_HOME/webapps/school-management-soap
sudo rm -f $TOMCAT_HOME/webapps/school-management-soap.war

# Copy WAR with proper permissions
sudo cp school-management-soap/target/school-management-soap.war $TOMCAT_HOME/webapps/
# sudo chown tomcat:tomcat $TOMCAT_HOME/webapps/school-management-soap.war
# sudo chmod 644 $TOMCAT_HOME/webapps/school-management-soap.war

echo "Démarrage de Tomcat..."
sudo systemctl start tomcat

echo "Waiting for Tomcat to start and deploy the application..."
sleep 5

# Check if application is deployed
if [ -d "$TOMCAT_HOME/webapps/school-management-soap" ]; then
    echo "Application deployed successfully!"
else
    echo "Warning: Application directory not found yet, waiting..."
    sleep 3
fi

echo "Tomcat service status :"
sudo systemctl --no-pager -l status tomcat | head -n 20

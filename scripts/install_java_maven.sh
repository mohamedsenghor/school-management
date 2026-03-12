#!/bin/bash
echo "Installation de Java 17 et Maven..."
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk maven
java -version
mvn -version

# Set JAVA_HOME environment variable in /etc/environment
echo 'JAVA_HOME="/usr/lib/jvm/java-17-openjdk-arm64"' | sudo tee -a /etc/environment
source /etc/environment
echo "JAVA_HOME set to: $JAVA_HOME"

echo "Java 17 et Maven sont installés avec succès!"

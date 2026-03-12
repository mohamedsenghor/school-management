#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

TOMCAT_VERSION=10.1.52
TOMCAT_HOME=/opt/tomcat10

echo -e "${YELLOW}Starting Tomcat 10 Installation...${NC}"

echo -e "${YELLOW}Creating tomcat group and user...${NC}"
# Create a dedicated user and group for Tomcat if they don't exist
sudo groupadd -f tomcat
sudo useradd -s /bin/false -g tomcat -d $TOMCAT_HOME tomcat || true

sudo pkill -f "org.apache.catalina.startup.Bootstrap" || true
sleep 2

echo -e "${YELLOW}Downloading Apache Tomcat $TOMCAT_VERSION...${NC}"
cd /tmp
wget https://dlcdn.apache.org/tomcat/tomcat-10/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

echo -e "${YELLOW}Extracting Tomcat to $TOMCAT_HOME...${NC}"
sudo mkdir -p $TOMCAT_HOME
cd $TOMCAT_HOME
sudo tar xzvf "/tmp/apache-tomcat-$TOMCAT_VERSION.tar.gz" -C $TOMCAT_HOME --strip-components=1

echo -e "${YELLOW}Setting permissions for Tomcat directories...${NC}"
sudo chgrp -R tomcat $TOMCAT_HOME
sudo chmod -R g+r conf
sudo chmod g+x conf
sudo chown -R tomcat webapps/ work/ temp/ logs/
sudo chmod +x $TOMCAT_HOME/bin/*.sh

# Load the JAVA_HOME environment variable
JAVA_HOME=$(update-alternatives --list java | head -1 | xargs dirname | xargs dirname)
echo -e "${YELLOW}JAVA_HOME is set to: $JAVA_HOME${NC}"

echo -e "${YELLOW}Creating systemd service for Tomcat...${NC}"
sudo tee /etc/systemd/system/tomcat.service << EOF
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking
PIDFile=$TOMCAT_HOME/temp/tomcat.pid

Environment=JAVA_HOME=$JAVA_HOME
Environment=CATALINA_PID=$TOMCAT_HOME/temp/tomcat.pid
Environment=CATALINA_HOME=$TOMCAT_HOME
Environment=CATALINA_BASE=$TOMCAT_HOME
Environment='CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC'
Environment='JAVA_OPTS=-Dawt.headless=true -Djava.security.egd=file:/dev/urandom'

ExecStart=$TOMCAT_HOME/bin/startup.sh
ExecStop=$TOMCAT_HOME/bin/shutdown.sh

User=tomcat
Group=tomcat
UMask=0007
RestartSec=10
Restart=on-failure

[Install]

WantedBy=multi-user.target
EOF

echo -e "${YELLOW}Reloading systemd daemon...${NC}"
sudo systemctl daemon-reload

# Create environment variables file
echo -e "${YELLOW}Setting up environment variables...${NC}"
ENV_FILE="/etc/environment"

echo -e "${YELLOW}Updating ${ENV_FILE} without duplicates...${NC}"
sudo touch "$ENV_FILE"

# Remove existing Tomcat/JAVA entries to keep the file idempotent
for key in CATALINA_PID CATALINA_HOME CATALINA_BASE CATALINA_OPTS JAVA_OPTS; do
    sudo sed -i "/^${key}=/d" "$ENV_FILE"
done

# Add fresh values once
sudo tee -a "$ENV_FILE" > /dev/null <<EOF
CATALINA_PID=$TOMCAT_HOME/temp/tomcat.pid
CATALINA_HOME=$TOMCAT_HOME
CATALINA_BASE=$TOMCAT_HOME
CATALINA_OPTS="-Xms512M -Xmx1024M -server -XX:+UseParallelGC"
JAVA_OPTS="-Dawt.headless=true -Djava.security.egd=file:/dev/urandom"
EOF

echo -e "${YELLOW}Configuring Tomcat Web Management Interface...${NC}"
sudo tee $TOMCAT_HOME/conf/tomcat-users.xml > /dev/null <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
              version="1.0">
  <role rolename="manager-gui"/>
  <role rolename="admin-gui"/>
  <user username="admin" password="admin" roles="manager-gui,admin-gui"/>
</tomcat-users>
EOF

echo -e "${YELLOW}Configuring Tomcat Manager access...${NC}"
sudo tee $TOMCAT_HOME/webapps/host-manager/META-INF/context.xml > /dev/null <<'EOF'
<Context antiResourceLocking="false" privileged="true" >
    <CookieProcessor className="org.apache.tomcat.util.http.Rfc6265CookieProcessor"
                                     sameSiteCookies="strict" />
    <Valve className="org.apache.catalina.valves.RemoteCIDRValve"
                 allow="0.0.0.0/0,::/0" />
    <Manager sessionAttributeValueClassNameFilter="java\.lang\.(?:Boolean|Integer|Long|Number|String)|org\.apache\.catalina\.filters\.CsrfPreventionFilter" />
</Context>
EOF

sudo mkdir -p $TOMCAT_HOME/conf/Catalina/localhost



echo -e "${GREEN}Tomcat 10 Installation Completed!${NC}"

# ETAPE 1 : Construction (Builder)
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Arguments de build (Valeurs par défaut)
ENV DB_HOST=mysql-db
ENV DB_PORT=3306
ENV DB_NAME=classes_management_db
ENV DB_USERNAME=user
ENV DB_PASSWORD=passer123@

# 1. Optimisation du cache Maven
COPY pom.xml .
COPY school-management-metier/pom.xml school-management-metier/
COPY school-management-soap/pom.xml school-management-soap/

RUN mvn dependency:go-offline -B

# 2. Copie des sources
COPY school-management-metier/src school-management-metier/src
COPY school-management-soap/src school-management-soap/src

# 3. SEQUENCE DE CONFIGURATION DE LA DATABASE
# On se place dans le dossier des ressources du module métier
WORKDIR /app/school-management-metier/src/main/resources

# A. Sauvegarder l'original
RUN cp database.properties database.properties.save

# B. Ecraser database.properties par le template docker
RUN cp database.properties.docker database.properties

# C. Appliquer les remplacements avec sed
RUN sed -i "s|\${DB_USERNAME}|${DB_USERNAME}|g" database.properties && \
    sed -i "s|\${DB_PASSWORD}|${DB_PASSWORD}|g" database.properties && \
    sed -i "s|\${DB_HOST}|${DB_HOST}|g" database.properties && \
    sed -i "s|\${DB_PORT}|${DB_PORT}|g" database.properties && \
    sed -i "s|\${DB_NAME}|${DB_NAME}|g" database.properties

# 4. Retour à la racine pour le build Maven
WORKDIR /app
RUN mvn clean package -DskipTests

# ============================================

# ETAPE 2 : Image finale (Runtime Tomcat)
FROM tomcat:10.1-jdk21

# Nettoyage des webapps par défaut
RUN rm -rf /usr/local/tomcat/webapps/*

# Copie du WAR compilé (qui contient maintenant le database.properties injecté)
COPY --from=builder /app/school-management-soap/target/school-management-soap.war /usr/local/tomcat/webapps/

EXPOSE 8080

# Lancement direct de Tomcat
CMD ["catalina.sh", "run"]

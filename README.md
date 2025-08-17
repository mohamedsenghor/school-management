# School Management Project

Ce projet est une application de gestion d'école basée sur Java, Spring, Hibernate, MySQL et expose des services SOAP.

## Prérequis

- Java 17+
- Maven 3.6+
- MySQL 8+
- Docker (optionnel, recommandé pour la base de données)
- SOAP UI (pour tester les API SOAP)

## 1. Cloner le projet

```bash
git clone https://github.com/mohamedsenghor/school-management.git
cd school-management
```

## 2. Démarrer la base de données

### Avec Docker (recommandé)

```bash
cd docker
docker-compose up -d
```

Cela démarre un conteneur MySQL accessible sur `localhost:3306`.

### Sans Docker

- Installez MySQL localement.
- Créez la base avec le script :

```bash
mysql -u root -p < scripts/create_database.sql
```

## 3. Configurer la connexion à la base de données

Vérifiez/modifiez le fichier `school-management-metier/src/main/resources/database.properties` selon vos paramètres MySQL.

## 4. Compiler et packager le projet

```bash
mvn clean install
```

## 5. Déployer et lancer le service SOAP

- Déployez le fichier WAR `school-management-soap/target/school-management-soap.war` sur un serveur compatible (Tomcat, Payara, etc.)
- Ou lancez via un IDE compatible Java EE.

Le service SOAP sera accessible à une URL du type :

```text
http://localhost:8080/school-management-soap/
```

## 6. Tester les API SOAP avec SOAP UI

### Importer le WSDL

- Ouvrez SOAP UI
- Créez un nouveau projet SOAP
- Importez le WSDL disponible à l’URL :

```text
http://localhost:8080/school-management-soap/wsdl/SchoolManagementService.wsdl
```

### Exemples de requêtes

#### 1. Récupérer tous les secteurs (`getAllSectors`)

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getAllSectors/>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 2. Créer un secteur (`saveSector`)

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:saveSector>
         <sector>
            <name>Esthétique</name>
         </sector>
      </sch:saveSector>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 3. Récupérer une classe (`getClass`)

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getClass>
         <id>1</id>
      </sch:getClass>
   </soapenv:Body>
</soapenv:Envelope>
```

#### 4. Créer une classe (`saveClass`)

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://bassine.com/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:saveClass>
         <class>
            <className>Biologie</className>
            <description>Cours de biologie générale</description>
            <sectorId>1</sectorId>
         </class>
      </sch:saveClass>
   </soapenv:Body>
</soapenv:Envelope>
```

## 7. Aller plus loin

- Consultez les guides dans le dossier `guides/` pour plus de détails sur l’architecture, le déploiement, etc.
- Modifiez les scripts SQL ou le code source selon vos besoins.

---

**Auteur :** Bassine

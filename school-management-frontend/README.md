# School Management Frontend

Frontend React JS pour le système de gestion scolaire.

## Prérequis

- Node.js >= 16
- npm ou yarn

## Installation

```bash
npm install
```

## Développement

Lancer le serveur de développement :

```bash
npm run dev
```

L'application sera accessible à `http://localhost:3000`

## Build pour la production

```bash
npm run build
```

Les fichiers compilés seront dans le dossier `dist/`

## Configuration

Les variables d'environnement peuvent être définies dans un fichier `.env` ou `.env.local` :

```txt
REACT_APP_API_URL=http://localhost:8080/school-management-soap/
```

## Structure du projet

```txt
src/
├── components/        # Composants React
│   ├── ClassesList.jsx
│   └── SectorsList.jsx
├── services/         # Services API
│   └── api.js       # Appels SOAP
├── styles/          # CSS
├── App.jsx          # Composant principal
├── index.css        # Styles globaux
└── main.jsx         # Entrée de l'app
```

## Fonctionnalités

- Affichage des classes
- Affichage des secteurs
- Navigation par onglets
- Design responsive
- Géré via SOAP Web Service

## Technologies

- React 18
- Vite
- Axios
- CSS3

## Notes

- Le frontend communique avec le backend via SOAP
- Assurez-vous que le service SOAP du backend est accessible
- Les appels SOAP sont proxifiés via Vite en développement

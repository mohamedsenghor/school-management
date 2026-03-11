# Multi-stage build for school-management-frontend
FROM node:20-alpine AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration des dépendances
COPY package.json package-lock.json* ./

# Installer les dépendances
RUN npm i

# Copier le reste du code source
COPY . .

ENV VITE_API_URL=/school-management-soap/

# Build de l'application
RUN npm run build

# Stage 2: Runtime avec Nginx
FROM nginx:1.25-alpine

# Copier les fichiers buildés depuis le builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exposer le port 3000
EXPOSE 3000

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]

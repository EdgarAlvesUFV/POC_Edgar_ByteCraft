# -----------------------------
# Etapa base (Node Debian)
# -----------------------------
FROM node:20 AS base
WORKDIR /app
COPY package*.json ./
RUN npm install

# -----------------------------
# Etapa desenvolvimento
# -----------------------------
FROM base AS dev
COPY . .
CMD ["npm", "run", "dev", "--", "--host"]

# -----------------------------
# Etapa build (produção)
# -----------------------------
FROM base AS build
COPY . .
RUN npm run build

# -----------------------------
# Etapa produção (Nginx)
# -----------------------------
FROM nginx:alpine AS prod
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]



# ===== build =====
FROM node:20-alpine AS build
WORKDIR /app

# 1) Dependências (cache amigável)
COPY package*.json ./
RUN npm ci

# 2) Variável de build pro Vite (padrão -> localhost:8080)
ARG VITE_API_URL=http://localhost:8080
ENV VITE_API_URL=${VITE_API_URL}

# 3) Código fonte
COPY . .

# 4) Build de produção
ENV NODE_ENV=production
RUN npm run build

# ===== serve estático =====
FROM nginx:1.27-alpine AS serve

# 5) Copia o bundle
COPY --from=build /app/dist /usr/share/nginx/html

# 6) Config Nginx (SPA fallback + cache básico)
RUN <<'NGINX' cat >/etc/nginx/conf.d/default.conf
server {
  listen 80;
  server_name _;

  root   /usr/share/nginx/html;
  index  index.html;

  # SPA fallback (react-router)
  location / {
    try_files $uri $uri/ /index.html;
  }

  # Cache estático básico
  location ~* \.(js|css|png|jpg|jpeg|gif|svg|woff2?)$ {
    add_header Cache-Control "public, max-age=31536000, immutable";
  }
}
NGINX

EXPOSE 80
CMD ["nginx","-g","daemon off;"]

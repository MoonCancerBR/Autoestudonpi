# Guia de Deploy - Sistema de Gerenciamento de Produtos e Categorias

## 🚀 Deploy em Produção

### Opção 1: Deploy Local com Systemd (Linux)

#### Backend Spring Boot

1. **Copiar JAR para local de produção:**
```bash
sudo mkdir -p /opt/produtos-api
sudo cp backend/target/produtos-api-1.0.0.jar /opt/produtos-api/
```

2. **Criar arquivo de serviço systemd:**
```bash
sudo tee /etc/systemd/system/produtos-api.service > /dev/null << EOF
[Unit]
Description=Produtos API
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/produtos-api
ExecStart=/usr/bin/java -jar /opt/produtos-api/produtos-api-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

3. **Habilitar e iniciar o serviço:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable produtos-api
sudo systemctl start produtos-api
sudo systemctl status produtos-api
```

#### Frontend Vue.js

1. **Compilar para produção:**
```bash
cd frontend
npm run build
```

2. **Servir com Nginx:**
```bash
sudo apt-get install nginx

# Criar arquivo de configuração
sudo tee /etc/nginx/sites-available/produtos-frontend > /dev/null << EOF
server {
    listen 80;
    server_name seu-dominio.com;

    root /var/www/produtos-frontend;
    index index.html;

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
    }
}
EOF

# Habilitar site
sudo ln -s /etc/nginx/sites-available/produtos-frontend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

3. **Copiar arquivos compilados:**
```bash
sudo mkdir -p /var/www/produtos-frontend
sudo cp -r frontend/dist/* /var/www/produtos-frontend/
```

### Opção 2: Deploy com Docker

1. **Criar Dockerfile para Backend:**
```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY backend/target/produtos-api-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

2. **Criar Dockerfile para Frontend:**
```dockerfile
FROM node:18-alpine as build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

3. **Criar docker-compose.yml para produção:**
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: senha_segura_aqui
      POSTGRES_DB: produtos_db
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - produtos_network

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/produtos_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: senha_segura_aqui
    depends_on:
      - postgres
    networks:
      - produtos_network

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - produtos_network

volumes:
  postgres_data:

networks:
  produtos_network:
    driver: bridge
```

4. **Executar com Docker Compose:**
```bash
docker-compose -f docker-compose.yml up -d
```

### Opção 3: Deploy em Plataforma em Nuvem

#### Heroku

**Backend:**
```bash
# Criar app
heroku create seu-app-backend

# Adicionar PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Deploy
git push heroku main
```

**Frontend:**
```bash
# Criar app
heroku create seu-app-frontend

# Configurar buildpack
heroku buildpacks:set heroku/nodejs

# Deploy
git push heroku main
```

#### AWS (EC2 + RDS)

1. **Criar instância EC2 com Ubuntu 20.04**
2. **Instalar dependências:**
```bash
sudo apt-get update
sudo apt-get install -y openjdk-11-jdk nodejs npm nginx
```

3. **Configurar RDS PostgreSQL**
4. **Atualizar application.properties com URL do RDS**
5. **Deploy seguindo passos do Systemd acima**

#### Google Cloud Platform

1. **Usar Cloud Run para backend:**
```bash
gcloud run deploy produtos-api \
  --source . \
  --platform managed \
  --region us-central1
```

2. **Usar Firebase Hosting para frontend:**
```bash
npm install -g firebase-tools
firebase init hosting
firebase deploy
```

## 🔒 Configurações de Segurança

### 1. Variáveis de Ambiente em Produção

Nunca commitar senhas! Usar variáveis de ambiente:

```bash
# Backend
export SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
export SPRING_DATASOURCE_USERNAME=usuario
export SPRING_DATASOURCE_PASSWORD=senha_forte
export SPRING_PROFILES_ACTIVE=prod
```

### 2. HTTPS/SSL

```bash
# Usar Let's Encrypt com Certbot
sudo apt-get install certbot python3-certbot-nginx
sudo certbot certonly --nginx -d seu-dominio.com
```

### 3. Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 4. Backup do Banco de Dados

```bash
# Backup automático com cron
0 2 * * * pg_dump -U postgres produtos_db > /backups/produtos_db_$(date +\%Y\%m\%d).sql
```

## 📊 Monitoramento

### Logs

```bash
# Backend
sudo journalctl -u produtos-api -f

# Frontend (Nginx)
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### Health Check

```bash
# Verificar se backend está respondendo
curl http://localhost:8080/api/categorias

# Verificar se frontend está respondendo
curl http://localhost
```

## 🔄 CI/CD com GitHub Actions

Criar `.github/workflows/deploy.yml`:

```yaml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Backend
        run: |
          cd backend
          mvn clean package -DskipTests
      
      - name: Build Frontend
        run: |
          cd frontend
          npm install
          npm run build
      
      - name: Deploy
        run: |
          # Adicionar scripts de deploy aqui
          echo "Deploying..."
```

## 📝 Checklist de Deploy

- [ ] Banco de dados configurado
- [ ] Variáveis de ambiente definidas
- [ ] HTTPS/SSL configurado
- [ ] Firewall configurado
- [ ] Backups configurados
- [ ] Monitoramento ativo
- [ ] Logs sendo coletados
- [ ] Health checks funcionando
- [ ] Testes de carga realizados
- [ ] Documentação atualizada

## 🆘 Troubleshooting

### Backend não inicia

```bash
# Verificar logs
sudo journalctl -u produtos-api -n 50

# Verificar porta
sudo netstat -tlnp | grep 8080
```

### Frontend não carrega

```bash
# Verificar Nginx
sudo nginx -t
sudo systemctl restart nginx

# Verificar permissões
sudo chown -R www-data:www-data /var/www/produtos-frontend
```

### Conexão com banco de dados falha

```bash
# Testar conexão
psql -h host -U usuario -d produtos_db

# Verificar variáveis de ambiente
echo $SPRING_DATASOURCE_URL
```

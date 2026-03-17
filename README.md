# Sistema de Gerenciamento de Produtos e Categorias

Um sistema full-stack completo para gerenciar produtos e categorias com backend em **Spring Boot** e frontend em **Vue.js + Vuetify**.

## 📋 Características

### Backend (Spring Boot + PostgreSQL)
- ✅ API REST completa para Produtos (POST, GET, PUT, DELETE)
- ✅ API REST completa para Categorias (POST, GET, PUT, DELETE)
- ✅ Validações de negócio (nome obrigatório, preço positivo, categoria obrigatória)
- ✅ Relacionamento ManyToOne entre Produto e Categoria usando JPA
- ✅ Paginação de produtos
- ✅ Tratamento de erros com mensagens descritivas

### Frontend (Vue.js + Vuetify)
- ✅ Interface responsiva com componentes Vuetify
- ✅ Listagem de categorias com CRUD completo
- ✅ Listagem de produtos com paginação
- ✅ Formulários com validações em tempo real
- ✅ Select dinâmico de categorias ao cadastrar produtos
- ✅ Mensagens de sucesso e erro
- ✅ Diálogos de confirmação para deleção

## 🚀 Como Executar

### Pré-requisitos
- Java 11+
- Maven 3.6+
- Node.js 16+
- PostgreSQL 12+

### 1. Configurar Banco de Dados PostgreSQL

```bash
# Criar banco de dados
createdb produtos_db

# Ou via psql
psql -U postgres
CREATE DATABASE produtos_db;
```

### 2. Executar Backend Spring Boot

```bash
cd backend

# Compilar e executar
mvn clean compile
mvn spring-boot:run

# Ou executar diretamente
mvn clean package
java -jar target/produtos-api-1.0.0.jar
```

O backend estará disponível em: `http://localhost:8080/api`

### 3. Executar Frontend Vue.js

```bash
cd frontend

# Instalar dependências (se não instaladas)
npm install

# Executar em modo desenvolvimento
npm run dev

# Ou compilar para produção
npm run build
```

O frontend estará disponível em: `http://localhost:5173`

## 📁 Estrutura do Projeto

```
crud-produtos-categorias/
├── backend/                          # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── controller/       # Controllers REST
│   │   │   │   ├── entity/           # Entidades JPA
│   │   │   │   ├── repository/       # Repositórios JPA
│   │   │   │   ├── service/          # Serviços de negócio
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   └── ProdutosApiApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── frontend/                         # Frontend Vue.js
    ├── src/
    │   ├── views/
    │   │   ├── Home.vue              # Página inicial
    │   │   ├── CategoriasList.vue    # CRUD de categorias
    │   │   └── ProdutosList.vue      # CRUD de produtos
    │   ├── services/
    │   │   └── api.js                # Cliente HTTP/Axios
    │   ├── router/
    │   │   └── index.js              # Configuração de rotas
    │   ├── App.vue                   # Componente raiz
    │   └── main.js                   # Ponto de entrada
    ├── package.json
    ├── vite.config.js
    └── index.html
```

## 🔌 Endpoints da API

### Categorias
- `GET /api/categorias` - Listar todas as categorias
- `GET /api/categorias/{id}` - Obter categoria por ID
- `POST /api/categorias` - Criar nova categoria
- `PUT /api/categorias/{id}` - Atualizar categoria
- `DELETE /api/categorias/{id}` - Deletar categoria

### Produtos
- `GET /api/produtos?page=0&size=10` - Listar produtos com paginação
- `GET /api/produtos/{id}` - Obter produto por ID
- `POST /api/produtos` - Criar novo produto
- `PUT /api/produtos/{id}` - Atualizar produto
- `DELETE /api/produtos/{id}` - Deletar produto

## 📝 Exemplos de Requisições

### Criar Categoria
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome": "Eletrônicos"}'
```

### Criar Produto
```bash
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook",
    "preco": 2500.00,
    "categoriaId": 1,
    "descricao": "Notebook de alta performance"
  }'
```

## ✅ Validações

### Categoria
- Nome: obrigatório, único, máximo 100 caracteres

### Produto
- Nome: obrigatório, máximo 150 caracteres
- Preço: obrigatório, deve ser maior que zero
- Categoria: obrigatória, deve existir no banco

## 🎨 Tecnologias Utilizadas

### Backend
- Spring Boot 2.7.15
- Spring Data JPA
- PostgreSQL
- Lombok
- Maven

### Frontend
- Vue.js 3
- Vuetify 4
- Vite
- Axios
- Vue Router

## 📄 Licença

MIT

## 👨‍💻 Autor

Sistema desenvolvido como projeto de gerenciamento de produtos e categorias.

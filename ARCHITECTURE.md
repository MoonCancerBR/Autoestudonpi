# 🏗️ Arquitetura do Sistema

## Visão Geral

O sistema segue uma arquitetura **cliente-servidor** com separação clara entre frontend e backend.

```
┌─────────────────────────────────────────────────────────────┐
│                     Cliente (Frontend)                       │
│              Vue.js 3 + Vuetify + TypeScript                 │
│                   http://localhost:5173                      │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
                         │ JSON
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  API REST (Backend)                          │
│         Spring Boot 2.7 + Spring Data JPA                    │
│              http://localhost:8080/api                       │
└────────────────────────┬────────────────────────────────────┘
                         │ JDBC
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Banco de Dados                              │
│              PostgreSQL 12+                                  │
│         postgresql://localhost:5432/produtos_db              │
└─────────────────────────────────────────────────────────────┘
```

## Backend - Arquitetura em Camadas

```
┌──────────────────────────────────────────────┐
│         Controller (REST Endpoints)           │
│  CategoriaController | ProdutoController      │
└────────────────┬─────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────┐
│      Service (Lógica de Negócio)              │
│  CategoriaService | ProdutoService            │
└────────────────┬─────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────┐
│     Repository (Acesso a Dados)               │
│  CategoriaRepository | ProdutoRepository      │
└────────────────┬─────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────┐
│      Entity (Modelo de Dados)                 │
│  Categoria | Produto                          │
└──────────────────────────────────────────────┘
```

## Frontend - Estrutura de Componentes

```
App.vue (Componente Raiz)
├── AppBar (Navegação)
├── Router View
│   ├── Home.vue (Página Inicial)
│   ├── CategoriasList.vue (CRUD Categorias)
│   │   ├── Tabela de Categorias
│   │   ├── Diálogo de Formulário
│   │   └── Diálogo de Confirmação
│   └── ProdutosList.vue (CRUD Produtos)
│       ├── Tabela de Produtos
│       ├── Paginação
│       ├── Diálogo de Formulário
│       └── Diálogo de Confirmação
└── Footer
```

## Fluxo de Dados

### Criar Categoria

```
Frontend                          Backend                    Database
   │                                 │                           │
   ├─ Validação Local ──────────────►│                           │
   │                                 │                           │
   │                         Validação Backend                    │
   │                                 │                           │
   │                         Salvar em BD ─────────────────────►│
   │                                 │                           │
   │◄─ Resposta JSON ────────────────┤                           │
   │                                 │                           │
   ├─ Atualizar UI                   │                           │
   │                                 │                           │
   └─ Mostrar Mensagem Sucesso       │                           │
```

### Listar Produtos com Paginação

```
Frontend                          Backend                    Database
   │                                 │                           │
   ├─ Requisição GET ────────────────►│                           │
   │  (page=0, size=10)              │                           │
   │                                 │                           │
   │                         Query com Paginação                  │
   │                                 │                           │
   │                         Buscar Dados ────────────────────►│
   │                                 │                           │
   │                         Construir Response                   │
   │                                 │                           │
   │◄─ JSON com Página ──────────────┤                           │
   │  {                              │                           │
   │    content: [...],              │                           │
   │    totalPages: 5,               │                           │
   │    totalElements: 45            │                           │
   │  }                              │                           │
   │                                 │                           │
   └─ Renderizar Tabela              │                           │
```

## Relacionamento de Dados

### Diagrama ER (Entity-Relationship)

```
┌──────────────────────┐         ┌──────────────────────┐
│     CATEGORIAS       │         │     PRODUTOS         │
├──────────────────────┤         ├──────────────────────┤
│ id (PK)              │◄────────│ id (PK)              │
│ nome (UNIQUE)        │    1:N  │ nome                 │
│ created_at           │         │ preco                │
│ updated_at           │         │ categoria_id (FK)    │
└──────────────────────┘         │ descricao            │
                                 │ created_at           │
                                 │ updated_at           │
                                 └──────────────────────┘
```

### Relacionamento ManyToOne

```java
// Em Produto.java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "categoria_id", nullable = false)
private Categoria categoria;
```

## Fluxo de Requisições HTTP

### Endpoints de Categorias

```
GET    /api/categorias           → Listar todas
GET    /api/categorias/{id}      → Obter por ID
POST   /api/categorias           → Criar
PUT    /api/categorias/{id}      → Atualizar
DELETE /api/categorias/{id}      → Deletar
```

### Endpoints de Produtos

```
GET    /api/produtos?page=0&size=10  → Listar com paginação
GET    /api/produtos/{id}             → Obter por ID
POST   /api/produtos                  → Criar
PUT    /api/produtos/{id}             → Atualizar
DELETE /api/produtos/{id}             → Deletar
```

## Validações

### Backend (Spring Validation)

```
Categoria:
├── nome: @NotNull, @NotBlank, @UniqueConstraint
└── Validação customizada em CategoriaService

Produto:
├── nome: @NotNull, @NotBlank
├── preco: @NotNull, @Positive
├── categoria: @NotNull
└── Validação customizada em ProdutoService
```

### Frontend (Vuetify Rules)

```
Categoria:
└── nome: required, not empty

Produto:
├── nome: required, not empty
├── preco: required, positive
└── categoria: required
```

## Tratamento de Erros

### Backend

```java
try {
    // Operação
} catch (IllegalArgumentException e) {
    return ResponseEntity
        .badRequest()
        .body(new ErrorResponse("Erro", e.getMessage()));
}
```

### Frontend

```javascript
try {
    await service.criar(dados)
} catch (error) {
    const mensagem = error.response?.data?.mensagem
    mostrarMensagem('error', mensagem)
}
```

## Segurança

### CORS (Cross-Origin Resource Sharing)

```java
registry.addMapping("/**")
    .allowedOrigins("http://localhost:5173", "http://localhost:3000")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true)
    .maxAge(3600);
```

### Validação de Entrada

- Frontend: Validação em tempo real
- Backend: Validação obrigatória com anotações
- Database: Constraints (NOT NULL, UNIQUE, etc)

## Performance

### Otimizações

1. **Paginação**: Produtos carregados em páginas de 10 itens
2. **Eager Loading**: Categoria carregada com Produto
3. **Índices**: Banco otimizado com índices nas chaves estrangeiras
4. **Caching**: Possibilidade de adicionar cache futuro

## Escalabilidade

### Melhorias Futuras

1. **Autenticação**: JWT ou OAuth2
2. **Autorização**: Role-based access control (RBAC)
3. **Logging**: Centralized logging com ELK Stack
4. **Monitoramento**: Prometheus + Grafana
5. **Cache**: Redis para dados frequentes
6. **Fila de Mensagens**: RabbitMQ para operações assíncronas
7. **Containerização**: Docker + Kubernetes
8. **Testes**: Testes unitários, integração e E2E

## Tecnologias Utilizadas

### Backend
- **Framework**: Spring Boot 2.7.15
- **ORM**: Spring Data JPA + Hibernate
- **Validação**: Jakarta Validation
- **Banco**: PostgreSQL 12+
- **Build**: Maven
- **Linguagem**: Java 11

### Frontend
- **Framework**: Vue.js 3
- **UI**: Vuetify 4
- **Build**: Vite
- **HTTP**: Axios
- **Roteamento**: Vue Router
- **Linguagem**: JavaScript ES6+

### Infraestrutura
- **Banco de Dados**: PostgreSQL
- **Containerização**: Docker (opcional)
- **Orquestração**: Docker Compose (opcional)
- **Servidor Web**: Nginx (produção)

## Diagrama de Sequência - Criar Produto

```
Frontend          Backend           Database
   │                 │                  │
   ├─ POST /produtos─►│                  │
   │ {nome, preco,    │                  │
   │  categoriaId}    │                  │
   │                  │                  │
   │            Validar dados            │
   │                  │                  │
   │            Buscar categoria         │
   │                  ├─ SELECT ────────►│
   │                  │◄─ categoria ─────┤
   │                  │                  │
   │            Salvar produto           │
   │                  ├─ INSERT ────────►│
   │                  │◄─ id ────────────┤
   │                  │                  │
   │◄─ 201 Created ───┤                  │
   │ {id, nome,       │                  │
   │  preco, ...}     │                  │
   │                  │                  │
   └─ Atualizar UI    │                  │
```

---

Esta arquitetura garante:
- ✅ Separação de responsabilidades
- ✅ Fácil manutenção e extensão
- ✅ Validações em múltiplas camadas
- ✅ Performance adequada
- ✅ Segurança básica implementada

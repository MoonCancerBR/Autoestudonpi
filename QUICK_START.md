# 🚀 Quick Start - Gerenciador de Produtos e Categorias

Comece em 5 minutos!

## 📋 Pré-requisitos

- Java 11+
- Maven 3.6+
- Node.js 16+
- PostgreSQL 12+

## ⚡ Iniciar Rapidamente

### Opção 1: Script Automático (Recomendado)

```bash
cd /home/ubuntu/crud-produtos-categorias-novo

# Iniciar tudo automaticamente
./start.sh

# Em outro terminal, para parar:
./stop.sh
```

### Opção 2: Manual

**Terminal 1 - Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

**Terminal 3 - Banco de Dados (opcional):**
```bash
# Se não estiver usando Docker
createdb produtos_db
```

## 🌐 Acessar a Aplicação

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **Banco de Dados**: postgresql://localhost:5432/produtos_db

## ✅ Teste Rápido

1. Abra http://localhost:5173 no navegador
2. Clique em "Categorias"
3. Clique em "Nova Categoria"
4. Digite um nome (ex: "Eletrônicos")
5. Clique em "Criar"
6. Vá para "Produtos"
7. Clique em "Novo Produto"
8. Preencha os dados e clique em "Criar"

## 📚 Documentação Completa

- [README.md](./README.md) - Visão geral do projeto
- [TESTING.md](./TESTING.md) - Guia de testes
- [DEVELOPMENT.md](./DEVELOPMENT.md) - Guia de desenvolvimento
- [DEPLOYMENT.md](./DEPLOYMENT.md) - Guia de deploy

## 🐛 Problemas Comuns

### Porta já em uso
```bash
# Matar processo na porta
lsof -ti:8080 | xargs kill -9
lsof -ti:5173 | xargs kill -9
```

### PostgreSQL não conecta
```bash
# Criar banco de dados
createdb produtos_db

# Ou verificar se está rodando
psql -U postgres -c "SELECT 1"
```

### Dependências não instaladas
```bash
# Backend
cd backend && mvn clean install

# Frontend
cd frontend && npm install
```

## 📞 Suporte

Consulte os arquivos de documentação para mais informações detalhadas.

---

**Pronto para começar?** Abra http://localhost:5173 e divirta-se! 🎉

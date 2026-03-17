# Guia de Testes - Sistema de Gerenciamento de Produtos e Categorias

## 🧪 Teste Manual Completo

### Pré-requisitos
1. PostgreSQL instalado e em execução
2. Backend compilado e em execução na porta 8080
3. Frontend em execução na porta 5173

### Passo 1: Iniciar o Banco de Dados

```bash
# Opção 1: Usando Docker Compose
docker-compose up -d

# Opção 2: PostgreSQL local
psql -U postgres -c "CREATE DATABASE produtos_db;" 2>/dev/null || true
```

### Passo 2: Iniciar o Backend

```bash
cd backend

# Opção 1: Usando Maven
mvn spring-boot:run

# Opção 2: Usando JAR compilado
java -jar target/produtos-api-1.0.0.jar
```

Você verá a mensagem:
```
Server running on http://localhost:8080/
```

### Passo 3: Iniciar o Frontend

Em outro terminal:

```bash
cd frontend
npm run dev
```

Você verá:
```
➜  Local:   http://localhost:5173/
```

### Passo 4: Acessar a Aplicação

Abra o navegador e acesse: `http://localhost:5173`

## ✅ Casos de Teste

### Teste 1: Criar Categoria

1. Clique em "Categorias" na navegação
2. Clique em "Nova Categoria"
3. Preencha o nome: "Eletrônicos"
4. Clique em "Criar"
5. ✅ Esperado: Mensagem de sucesso e categoria aparece na tabela

**Teste de Validação:**
- Tente criar sem preencher o nome → Deve mostrar erro de validação

### Teste 2: Editar Categoria

1. Na listagem de categorias, clique no ícone de edição
2. Altere o nome para "Eletrônicos e Acessórios"
3. Clique em "Atualizar"
4. ✅ Esperado: Categoria atualizada na tabela

### Teste 3: Deletar Categoria

1. Na listagem de categorias, clique no ícone de delete
2. Confirme a deleção
3. ✅ Esperado: Categoria removida da tabela

### Teste 4: Criar Produto

1. Clique em "Produtos" na navegação
2. Clique em "Novo Produto"
3. Preencha os campos:
   - Nome: "Notebook Dell"
   - Preço: 2500.00
   - Categoria: "Eletrônicos"
   - Descrição: "Notebook de alta performance"
4. Clique em "Criar"
5. ✅ Esperado: Produto criado com sucesso

**Testes de Validação:**
- Deixe o nome vazio → Deve mostrar erro
- Coloque preço negativo → Deve mostrar erro
- Não selecione categoria → Deve mostrar erro

### Teste 5: Listar Produtos com Paginação

1. Na página de Produtos, observe a tabela
2. Se houver mais de 10 produtos, clique nos números de página
3. ✅ Esperado: Produtos são carregados corretamente em cada página

### Teste 6: Editar Produto

1. Na listagem de produtos, clique no ícone de edição
2. Altere o preço para 3000.00
3. Clique em "Atualizar"
4. ✅ Esperado: Produto atualizado com novo preço

### Teste 7: Deletar Produto

1. Na listagem de produtos, clique no ícone de delete
2. Confirme a deleção
3. ✅ Esperado: Produto removido da tabela

### Teste 8: Responsividade

1. Redimensione a janela do navegador para tamanho mobile
2. ✅ Esperado: Layout se adapta corretamente

## 🔍 Testes via cURL

### Listar Categorias
```bash
curl -X GET http://localhost:8080/api/categorias
```

### Criar Categoria
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome": "Roupas"}'
```

### Listar Produtos (com paginação)
```bash
curl -X GET "http://localhost:8080/api/produtos?page=0&size=10"
```

### Criar Produto
```bash
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Camiseta",
    "preco": 50.00,
    "categoriaId": 1,
    "descricao": "Camiseta de algodão"
  }'
```

### Atualizar Categoria
```bash
curl -X PUT http://localhost:8080/api/categorias/1 \
  -H "Content-Type: application/json" \
  -d '{"nome": "Roupas Premium"}'
```

### Deletar Produto
```bash
curl -X DELETE http://localhost:8080/api/produtos/1
```

## 🐛 Troubleshooting

### Erro: "Connection refused" ao conectar com backend

**Solução:**
- Verifique se o backend está em execução na porta 8080
- Verifique se o PostgreSQL está em execução
- Verifique a URL no arquivo `frontend/src/services/api.js`

### Erro: "CORS policy" no console do navegador

**Solução:**
- O CORS já está configurado no backend
- Verifique se o frontend está acessando `http://localhost:8080/api`

### Erro: "Database connection failed"

**Solução:**
- Verifique se PostgreSQL está em execução
- Verifique as credenciais em `backend/src/main/resources/application.properties`
- Verifique se o banco `produtos_db` foi criado

### Erro: "Port 5173 already in use"

**Solução:**
```bash
# Matar processo na porta 5173
lsof -ti:5173 | xargs kill -9

# Ou usar outra porta
npm run dev -- --port 5174
```

## 📊 Dados de Teste

### Categorias Sugeridas
- Eletrônicos
- Roupas
- Alimentos
- Livros
- Móveis

### Produtos Sugeridos
- Notebook (Eletrônicos) - R$ 2500.00
- Camiseta (Roupas) - R$ 50.00
- Arroz (Alimentos) - R$ 5.00
- Clean Code (Livros) - R$ 89.90
- Mesa (Móveis) - R$ 300.00

## ✨ Checklist de Validação

- [ ] Backend compila sem erros
- [ ] Frontend compila sem erros
- [ ] PostgreSQL está em execução
- [ ] Criar categoria funciona
- [ ] Editar categoria funciona
- [ ] Deletar categoria funciona
- [ ] Criar produto funciona
- [ ] Editar produto funciona
- [ ] Deletar produto funciona
- [ ] Paginação de produtos funciona
- [ ] Validações funcionam (frontend e backend)
- [ ] Mensagens de sucesso aparecem
- [ ] Mensagens de erro aparecem
- [ ] Select de categorias carrega dinamicamente
- [ ] Interface é responsiva
- [ ] CORS está funcionando
- [ ] Relacionamento ManyToOne está correto

## 📝 Logs Úteis

### Ver logs do backend
```bash
# Logs em tempo real
tail -f backend/nohup.out

# Ou usar Maven
mvn spring-boot:run | grep -E "ERROR|WARN|INFO"
```

### Ver logs do frontend
```bash
# Abrir DevTools do navegador (F12)
# Aba Console mostra erros e logs
```

## 🎯 Próximos Passos (Opcional)

1. Adicionar autenticação de usuários
2. Adicionar paginação no frontend para categorias
3. Adicionar filtros e busca
4. Adicionar testes unitários
5. Adicionar testes de integração
6. Implementar cache
7. Adicionar documentação Swagger/OpenAPI
8. Deploy em produção

# Guia de Desenvolvimento - Sistema de Gerenciamento de Produtos e Categorias

## 🛠️ Ambiente de Desenvolvimento

### Pré-requisitos

- **Java 11+**: [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+**: [Download](https://maven.apache.org/download.cgi)
- **Node.js 16+**: [Download](https://nodejs.org/)
- **PostgreSQL 12+**: [Download](https://www.postgresql.org/download/)
- **Git**: [Download](https://git-scm.com/)

### Configuração Inicial

1. **Clonar o repositório:**
```bash
git clone <seu-repositorio>
cd crud-produtos-categorias
```

2. **Configurar banco de dados:**
```bash
# Criar banco de dados
createdb produtos_db

# Ou via psql
psql -U postgres
CREATE DATABASE produtos_db;
```

3. **Instalar dependências do backend:**
```bash
cd backend
mvn clean install
```

4. **Instalar dependências do frontend:**
```bash
cd frontend
npm install
```

## 🚀 Executar em Desenvolvimento

### Terminal 1: Backend Spring Boot

```bash
cd backend
mvn spring-boot:run
```

Saída esperada:
```
Server running on http://localhost:8080/
```

### Terminal 2: Frontend Vue.js

```bash
cd frontend
npm run dev
```

Saída esperada:
```
➜  Local:   http://localhost:5173/
```

### Terminal 3: PostgreSQL (opcional, se não usar Docker)

```bash
# Iniciar PostgreSQL (Linux/Mac)
pg_ctl -D /usr/local/var/postgres start

# Ou usar Docker
docker-compose up -d
```

## 📁 Estrutura de Código

### Backend

```
backend/
├── src/main/java/com/example/
│   ├── ProdutosApiApplication.java     # Classe principal
│   ├── controller/
│   │   ├── CategoriaController.java    # REST endpoints para categorias
│   │   └── ProdutoController.java      # REST endpoints para produtos
│   ├── entity/
│   │   ├── Categoria.java              # Entidade JPA
│   │   └── Produto.java                # Entidade JPA com ManyToOne
│   ├── repository/
│   │   ├── CategoriaRepository.java    # JPA Repository
│   │   └── ProdutoRepository.java      # JPA Repository
│   ├── service/
│   │   ├── CategoriaService.java       # Lógica de negócio
│   │   └── ProdutoService.java         # Lógica de negócio
│   └── dto/
│       ├── CategoriaDTO.java           # Data Transfer Object
│       └── ProdutoDTO.java             # Data Transfer Object
└── src/main/resources/
    └── application.properties           # Configurações
```

### Frontend

```
frontend/
├── src/
│   ├── views/
│   │   ├── Home.vue                    # Página inicial
│   │   ├── CategoriasList.vue          # CRUD de categorias
│   │   └── ProdutosList.vue            # CRUD de produtos
│   ├── services/
│   │   └── api.js                      # Cliente HTTP com Axios
│   ├── router/
│   │   └── index.js                    # Configuração de rotas
│   ├── App.vue                         # Componente raiz
│   └── main.js                         # Ponto de entrada
├── package.json
├── vite.config.js
└── index.html
```

## 🔄 Fluxo de Desenvolvimento

### Adicionar Nova Funcionalidade

#### 1. Backend

**Passo 1: Criar/Atualizar Entidade**
```java
@Entity
@Table(name = "nova_tabela")
public class NovaEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String campo;
}
```

**Passo 2: Criar Repository**
```java
@Repository
public interface NovaRepository extends JpaRepository<NovaEntidade, Long> {
}
```

**Passo 3: Criar Service**
```java
@Service
@RequiredArgsConstructor
public class NovaService {
    private final NovaRepository repository;
    
    public List<NovaDTO> listar() {
        return repository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
```

**Passo 4: Criar Controller**
```java
@RestController
@RequestMapping("/novas")
@RequiredArgsConstructor
public class NovaController {
    private final NovaService service;
    
    @GetMapping
    public ResponseEntity<List<NovaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
```

#### 2. Frontend

**Passo 1: Criar Serviço de API**
```javascript
// src/services/api.js
export const novaService = {
    listar() {
        return api.get('/novas')
    }
}
```

**Passo 2: Criar Componente Vue**
```vue
<template>
    <div>
        <v-btn @click="carregar">Carregar</v-btn>
        <v-list>
            <v-list-item v-for="item in items" :key="item.id">
                {{ item.nome }}
            </v-list-item>
        </v-list>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { novaService } from '@/services/api'

const items = ref([])

const carregar = async () => {
    const response = await novaService.listar()
    items.value = response.data
}

onMounted(() => carregar())
</script>
```

**Passo 3: Adicionar Rota**
```javascript
// src/router/index.js
{
    path: '/novas',
    name: 'Novas',
    component: () => import('../views/NovasList.vue')
}
```

## 🧪 Testes

### Backend - Testes Unitários

```bash
# Executar testes
mvn test

# Executar teste específico
mvn test -Dtest=CategoriaServiceTest
```

Exemplo de teste:
```java
@SpringBootTest
class CategoriaServiceTest {
    @Autowired
    private CategoriaService service;
    
    @Test
    void testCriarCategoria() {
        CategoriaDTO dto = new CategoriaDTO(null, "Teste");
        CategoriaDTO resultado = service.criar(dto);
        
        assertNotNull(resultado.getId());
        assertEquals("Teste", resultado.getNome());
    }
}
```

### Frontend - Testes com Vitest

```bash
# Executar testes
npm run test

# Modo watch
npm run test:watch
```

## 📝 Padrões de Código

### Backend

- **Naming**: camelCase para variáveis, PascalCase para classes
- **Validação**: Usar anotações `@NotNull`, `@NotBlank`, `@Positive`
- **Tratamento de Erro**: Usar `IllegalArgumentException` para erros de negócio
- **DTO**: Sempre usar DTOs para requisições/respostas
- **Transações**: Usar `@Transactional` em serviços

### Frontend

- **Naming**: camelCase para variáveis e funções, PascalCase para componentes
- **Componentes**: Usar Composition API com `<script setup>`
- **Reatividade**: Usar `ref` para estado reativo
- **Async**: Usar `async/await` em vez de `.then()`
- **Validação**: Validar no frontend e backend

## 🐛 Debug

### Backend

```bash
# Ativar debug no Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"

# Ou usar IDE (IntelliJ IDEA, Eclipse, VS Code)
# Adicionar breakpoints e executar em debug
```

### Frontend

```bash
# Abrir DevTools (F12)
# Aba Console: logs e erros
# Aba Network: requisições HTTP
# Aba Sources: debug com breakpoints

# Ou usar Vue DevTools
# Extensão do navegador para debug de Vue
```

## 📚 Recursos Úteis

### Backend
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Lombok](https://projectlombok.org/)

### Frontend
- [Vue.js 3 Documentation](https://vuejs.org/)
- [Vuetify Documentation](https://vuetifyjs.com/)
- [Vue Router](https://router.vuejs.org/)
- [Axios](https://axios-http.com/)

### Database
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [pgAdmin](https://www.pgadmin.org/) - Interface gráfica

## 🚨 Troubleshooting

### Maven não encontra dependências

```bash
# Limpar cache
mvn clean
rm -rf ~/.m2/repository

# Reinstalar
mvn install
```

### Porta 8080 já em uso

```bash
# Encontrar processo
lsof -i :8080

# Matar processo
kill -9 <PID>

# Ou usar outra porta
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Erro de CORS

- Verificar `ProdutosApiApplication.java`
- Adicionar origem do frontend em `corsConfigurer()`

### Banco de dados não conecta

```bash
# Verificar credenciais em application.properties
# Verificar se PostgreSQL está rodando
# Verificar se banco foi criado

psql -U postgres -l | grep produtos_db
```

## 📋 Checklist de Qualidade

Antes de fazer commit:

- [ ] Código compila sem erros
- [ ] Sem warnings do compilador
- [ ] Testes passam
- [ ] Código segue padrões do projeto
- [ ] Sem código comentado
- [ ] Sem console.log() no frontend
- [ ] Sem System.out.println() no backend
- [ ] Mensagens de erro são claras
- [ ] Validações funcionam
- [ ] Responsividade testada

## 🔗 Git Workflow

```bash
# Criar branch para feature
git checkout -b feature/nome-da-feature

# Fazer commits
git add .
git commit -m "feat: descrição da mudança"

# Push
git push origin feature/nome-da-feature

# Criar Pull Request no GitHub
```

## 📞 Suporte

Para dúvidas ou problemas:
1. Verificar documentação
2. Consultar logs
3. Executar testes
4. Abrir issue no GitHub

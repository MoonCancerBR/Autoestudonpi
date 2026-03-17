#!/bin/bash

# Script final para iniciar o projeto Produtos API
# Ubuntu/Debian, PostgreSQL, Spring Boot + Frontend

echo "================================"
echo "Iniciando Projeto - Produtos API"
echo "================================"
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para verificar se porta está em uso
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        return 0
    else
        return 1
    fi
}

# ==========================
# Variável de senha do PostgreSQL
# ==========================
export PGPASSWORD=123456

# ==========================
# Verificar PostgreSQL
# ==========================
echo -e "${YELLOW}Verificando PostgreSQL...${NC}"
if pg_isready -h localhost -p 5432 -U npi | grep -E -q "(accepting connections|aceitando conexões)"; then
    echo -e "${GREEN}✓ PostgreSQL está rodando${NC}"
else
    echo -e "${RED}✗ PostgreSQL não está rodando${NC}"
    echo "Inicie PostgreSQL e tente novamente"
    exit 1
fi

# ==========================
# Criar banco de dados se não existir
# ==========================
echo -e "${YELLOW}Verificando/ Criando banco de dados produtos_db...${NC}"
DB_EXISTS=$(psql -h localhost -U npi -tc "SELECT 1 FROM pg_database WHERE datname = 'produtos_db';" | xargs)
if [ "$DB_EXISTS" = "1" ]; then
    echo -e "${GREEN}✓ Banco de dados já existe${NC}"
else
    psql -h localhost -U npi -c "CREATE DATABASE produtos_db;"
    echo -e "${GREEN}✓ Banco de dados produtos_db criado${NC}"
fi

# ==========================
# Verificar portas
# ==========================
echo ""
echo -e "${YELLOW}Verificando portas...${NC}"

if check_port 8080; then
    echo -e "${RED}✗ Porta 8080 já está em uso${NC}"
    exit 1
else
    echo -e "${GREEN}✓ Porta 8080 disponível${NC}"
fi

if check_port 5173; then
    echo -e "${RED}✗ Porta 5173 já está em uso${NC}"
    exit 1
else
    echo -e "${GREEN}✓ Porta 5173 disponível${NC}"
fi

# ==========================
# Iniciar Backend
# ==========================
echo ""
echo -e "${YELLOW}Iniciando Backend...${NC}"
cd backend
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}✓ Backend iniciado (PID: $BACKEND_PID)${NC}"
echo "  URL: http://localhost:8080/api"
echo "  Logs: backend.log"

# Aguardar backend subir (até 20 segundos)
echo -e "${YELLOW}Aguardando backend iniciar...${NC}"
MAX_WAIT=20
for i in $(seq 1 $MAX_WAIT); do
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null; then
        echo -e "${GREEN}✓ Backend iniciou corretamente${NC}"
        break
    fi
    sleep 1
done

# ==========================
# Iniciar Frontend
# ==========================
echo ""
echo -e "${YELLOW}Iniciando Frontend...${NC}"
cd ../frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo -e "${GREEN}✓ Frontend iniciado (PID: $FRONTEND_PID)${NC}"
echo "  URL: http://localhost:5173"
echo "  Logs: frontend.log"

# Aguardar frontend iniciar (3s)
echo -e "${YELLOW}Aguardando frontend iniciar...${NC}"
sleep 3

# ==========================
# Mensagem final
# ==========================
echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}✓ Projeto iniciado com sucesso!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "URLs:"
echo "  Frontend:  http://localhost:5173"
echo "  Backend:   http://localhost:8080/api"
echo "  Banco:     postgresql://localhost:5432/produtos_db"
echo ""
echo "Para parar o projeto, execute: ./stop.sh"
echo ""
echo "Logs:"
echo "  Backend:   tail -f backend.log"
echo "  Frontend:  tail -f frontend.log"
echo ""

# Salvar PIDs para script de parada
echo $BACKEND_PID > .backend.pid
echo $FRONTEND_PID > .frontend.pid

# Aguardar indefinidamente
wait
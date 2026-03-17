#!/bin/bash

# Script para parar o projeto

echo "Parando Projeto..."

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Parar backend
if [ -f .backend.pid ]; then
    BACKEND_PID=$(cat .backend.pid)
    if kill -0 $BACKEND_PID 2>/dev/null; then
        echo -e "${YELLOW}Parando Backend (PID: $BACKEND_PID)...${NC}"
        kill $BACKEND_PID
        sleep 2
        echo -e "${GREEN}✓ Backend parado${NC}"
    fi
    rm .backend.pid
fi

# Parar frontend
if [ -f .frontend.pid ]; then
    FRONTEND_PID=$(cat .frontend.pid)
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        echo -e "${YELLOW}Parando Frontend (PID: $FRONTEND_PID)...${NC}"
        kill $FRONTEND_PID
        sleep 2
        echo -e "${GREEN}✓ Frontend parado${NC}"
    fi
    rm .frontend.pid
fi

echo -e "${GREEN}✓ Projeto parado com sucesso${NC}"

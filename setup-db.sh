#!/bin/bash

# Script para configurar o banco de dados PostgreSQL

echo "Criando banco de dados PostgreSQL..."

# Verifica se PostgreSQL está instalado
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL não está instalado. Por favor, instale PostgreSQL primeiro."
    exit 1
fi

# Cria o banco de dados
psql -U postgres -c "CREATE DATABASE produtos_db;" 2>/dev/null || echo "Banco de dados já existe ou erro ao criar."

echo "Banco de dados configurado com sucesso!"
echo "Conexão: postgresql://postgres:postgres@localhost:5432/produtos_db"

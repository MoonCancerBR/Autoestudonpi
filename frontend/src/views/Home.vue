<template>
  <div class="home">
    <v-card class="mb-6">
      <v-card-title class="text-h4">Bem-vindo ao Gerenciador de Produtos e Categorias</v-card-title>
      <v-card-text>
        <p class="text-body1 mb-4">
          Este sistema permite gerenciar produtos e categorias de forma simples e intuitiva.
        </p>
        <v-row>
          <v-col cols="12" md="6">
            <v-card class="pa-6 text-center cursor-pointer hover-card" @click="$router.push('/categorias')">
              <v-icon size="64" color="primary" class="mb-4">mdi-folder-multiple</v-icon>
              <v-card-title>Gerenciar Categorias</v-card-title>
              <v-card-text>
                Crie, edite e delete categorias de produtos.
              </v-card-text>
              <v-btn color="primary" variant="tonal">Acessar Categorias</v-btn>
            </v-card>
          </v-col>
          <v-col cols="12" md="6">
            <v-card class="pa-6 text-center cursor-pointer hover-card" @click="$router.push('/produtos')">
              <v-icon size="64" color="success" class="mb-4">mdi-package-multiple</v-icon>
              <v-card-title>Gerenciar Produtos</v-card-title>
              <v-card-text>
                Crie, edite e delete produtos com categorias.
              </v-card-text>
              <v-btn color="success" variant="tonal">Acessar Produtos</v-btn>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.hover-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.hover-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}
</style>

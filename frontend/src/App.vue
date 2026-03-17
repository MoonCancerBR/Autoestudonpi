<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <v-app-bar-title>Gerenciador de Produtos e Categorias</v-app-bar-title>
      <v-spacer></v-spacer>
      <v-btn text to="/" class="nav-btn">Home</v-btn>
      <v-btn text to="/categorias" class="nav-btn">Categorias</v-btn>
      <v-btn text to="/produtos" class="nav-btn">Produtos</v-btn>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-main>

    <v-footer app color="grey-lighten-2" class="text-center">
      <span>&copy; 2026 Gerenciador de Produtos. Todos os direitos reservados.</span>
    </v-footer>
  </v-app>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.nav-btn {
  margin: 0 8px;
}
</style>

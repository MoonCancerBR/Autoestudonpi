import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import CategoriasList from '../views/CategoriasList.vue'
import ProdutosList from '../views/ProdutosList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/categorias',
    name: 'Categorias',
    component: CategoriasList
  },
  {
    path: '/produtos',
    name: 'Produtos',
    component: ProdutosList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

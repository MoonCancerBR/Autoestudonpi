import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Categorias
export const categoriaService = {
  listarTodas() {
    return api.get('/categorias')
  },
  obterPorId(id) {
    return api.get(`/categorias/${id}`)
  },
  criar(categoria) {
    return api.post('/categorias', categoria)
  },
  atualizar(id, categoria) {
    return api.put(`/categorias/${id}`, categoria)
  },
  deletar(id) {
    return api.delete(`/categorias/${id}`)
  }
}

// Produtos
export const produtoService = {
  listar(page = 0, size = 10, nome = null, categoriaId = null) {
    return api.get('/produtos', {
      params: { page, size, nome, categoriaId }
    })
  },
  buscarPorNome(nome, page = 0, size = 10) {
    return api.get('/produtos', {
      params: { page, size, nome }
    })
  },
  buscarPorCategoria(categoriaId, page = 0, size = 10) {
    return api.get('/produtos', {
      params: { page, size, categoriaId }
    })
  },
  obterPorId(id) {
    return api.get(`/produtos/${id}`)
  },
  criar(produto) {
    return api.post('/produtos', produto)
  },
  atualizar(id, produto) {
    return api.put(`/produtos/${id}`, produto)
  },
  deletar(id) {
    return api.delete(`/produtos/${id}`)
  }
}

export default api
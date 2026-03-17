<template>
  <div class="produtos">
    <v-card class="mb-6">
      <v-card-title class="text-h5">Gerenciar Produtos</v-card-title>
      <v-card-text>
        <!-- Botão Novo Produto -->
        <v-btn color="primary" @click="abrirDialogoNovo" class="mb-4">
          <v-icon left>mdi-plus</v-icon>
          Novo Produto
        </v-btn>

        <!-- Barra de Busca e Filtros -->
        <v-row class="mb-4">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="filtroBusca"
              label="Buscar por nome"
              prepend-icon="mdi-magnify"
              clearable
              @input="aplicarFiltros"
              outlined
              dense
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="filtroCategoria"
              :items="categorias"
              item-title="nome"
              item-value="id"
              label="Filtrar por categoria"
              clearable
              @update:model-value="aplicarFiltros"
              outlined
              dense
            ></v-select>
          </v-col>
        </v-row>

        <!-- Tabela de Produtos -->
        <v-table v-if="produtos.length > 0" class="mt-4">
          <thead>
            <tr>
              <th class="text-left">Nome</th>
              <th class="text-left">Preço</th>
              <th class="text-left">Quantidade</th>
              <th class="text-left">Categoria</th>
              <th class="text-center">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="produto in produtos" :key="produto.id">
              <td>{{ produto.nome }}</td>
              <td>R$ {{ formatarPreco(produto.preco) }}</td>
              <td>{{ produto.quantidade }} un.</td>
              <td>{{ produto.categoriaNome }}</td>
              <td class="text-center">
                <v-btn size="small" color="warning" @click="editarProduto(produto)" class="mr-2" title="Editar">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn size="small" color="error" @click="confirmarDelecao(produto)" title="Deletar">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>

        <v-alert v-else type="info" class="mt-4">
          Nenhum produto cadastrado. Clique em "Novo Produto" para criar um.
        </v-alert>

        <!-- Paginação -->
        <v-pagination
          v-if="totalPaginas > 1"
          v-model="paginaAtual"
          :length="totalPaginas"
          @update:model-value="carregarProdutos"
          class="mt-4"
        ></v-pagination>

        <!-- Mensagens de Feedback -->
        <v-alert v-if="mensagem.tipo" :type="mensagem.tipo" class="mt-4" closable>
          {{ mensagem.texto }}
        </v-alert>
      </v-card-text>
    </v-card>

    <!-- Diálogo de Formulário -->
    <v-dialog v-model="dialogoAberto" max-width="600px">
      <v-card>
        <v-card-title>{{ editando ? 'Editar Produto' : 'Novo Produto' }}</v-card-title>
        <v-card-text>
          <v-form ref="formulario" @submit.prevent="salvarProduto">
            <v-text-field
              v-model="formularioProduto.nome"
              label="Nome do Produto"
              :rules="regrasNome"
              required
              outlined
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model.number="formularioProduto.preco"
              label="Preço"
              type="number"
              step="0.01"
              :rules="regrasPreco"
              required
              outlined
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model.number="formularioProduto.quantidade"
              label="Quantidade"
              type="number"
              step="1"
              :rules="regrasQuantidade"
              required
              outlined
              class="mb-4"
            ></v-text-field>

            <v-select
              v-model="formularioProduto.categoriaId"
              :items="categorias"
              item-title="nome"
              item-value="id"
              label="Categoria (opcional)"
              clearable
              outlined
              class="mb-4"
            ></v-select>

            <v-textarea
              v-model="formularioProduto.descricao"
              label="Descrição"
              outlined
              rows="3"
            ></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" @click="fecharDialogo">Cancelar</v-btn>
          <v-btn color="primary" @click="salvarProduto" :loading="carregando">
            {{ editando ? 'Atualizar' : 'Criar' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Diálogo de Confirmação de Deleção -->
    <v-dialog v-model="dialogoConfirmacao" max-width="400px">
      <v-card>
        <v-card-title>Confirmar Deleção</v-card-title>
        <v-card-text>
          Tem certeza que deseja deletar o produto "{{ produtoSelecionado?.nome }}"?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" @click="dialogoConfirmacao = false">Cancelar</v-btn>
          <v-btn color="error" @click="deletarProduto" :loading="carregando">Deletar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { produtoService, categoriaService } from '../services/api'

const produtos = ref([])
const categorias = ref([])
const dialogoAberto = ref(false)
const dialogoConfirmacao = ref(false)
const carregando = ref(false)
const editando = ref(false)
const formularioProduto = ref({ nome: '', preco: 0, quantidade: 1, categoriaId: null, descricao: '' })
const produtoSelecionado = ref(null)
const formulario = ref(null)
const mensagem = ref({ tipo: '', texto: '' })
const paginaAtual = ref(1)
const totalPaginas = ref(1)
const tamanhoPagina = 10
const filtroBusca = ref('')
const filtroCategoria = ref(null)

const regrasNome = [
  v => !!v || 'Nome é obrigatório',
  v => (v && v.length > 0) || 'Nome não pode estar vazio'
]

const regrasPreco = [
  v => v !== null && v !== undefined && v !== '' || 'Preço é obrigatório',
  v => (v && parseFloat(v) > 0) || 'Preço deve ser maior que zero'
]

const regrasQuantidade = [
  v => v !== null && v !== undefined && v !== '' || 'Quantidade é obrigatória',
  v => (v && parseInt(v) > 0) || 'Quantidade deve ser maior que zero'
]

onMounted(() => {
  carregarCategorias()
  carregarProdutos()
})

const carregarCategorias = async () => {
  try {
    const response = await categoriaService.listarTodas()
    categorias.value = response.data
  } catch (error) {
    console.error('Erro ao carregar categorias:', error)
  }
}

const carregarProdutos = async () => {
  try {
    carregando.value = true
    const response = await produtoService.listar(
      paginaAtual.value - 1,
      tamanhoPagina,
      filtroBusca.value || null,
      filtroCategoria.value || null
    )
    produtos.value = response.data.content
    totalPaginas.value = response.data.totalPages
  } catch (error) {
    mostrarMensagem('error', 'Erro ao carregar produtos')
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const aplicarFiltros = () => {
  paginaAtual.value = 1
  carregarProdutos()
}

const abrirDialogoNovo = () => {
  editando.value = false
  formularioProduto.value = { nome: '', preco: 0, quantidade: 1, categoriaId: null, descricao: '' }
  dialogoAberto.value = true
}

const editarProduto = (produto) => {
  editando.value = true
  produtoSelecionado.value = produto
  formularioProduto.value = {
    nome: produto.nome,
    preco: produto.preco,
    quantidade: produto.quantidade,
    categoriaId: produto.categoriaId,
    descricao: produto.descricao
  }
  dialogoAberto.value = true
}

const fecharDialogo = () => {
  dialogoAberto.value = false
  formularioProduto.value = { nome: '', preco: 0, quantidade: 1, categoriaId: null, descricao: '' }
  produtoSelecionado.value = null
}

const salvarProduto = async () => {
  if (!formulario.value || !await formulario.value.validate()) {
    mostrarMensagem('error', 'Por favor, preencha todos os campos obrigatórios corretamente')
    return
  }

  try {
    carregando.value = true
    const dados = {
      nome: formularioProduto.value.nome,
      preco: parseFloat(formularioProduto.value.preco),
      quantidade: parseInt(formularioProduto.value.quantidade),
      categoriaId: formularioProduto.value.categoriaId,
      descricao: formularioProduto.value.descricao
    }

    if (editando.value) {
      await produtoService.atualizar(produtoSelecionado.value.id, dados)
      mostrarMensagem('success', 'Produto atualizado com sucesso!')
    } else {
      await produtoService.criar(dados)
      mostrarMensagem('success', 'Produto criado com sucesso!')
    }
    fecharDialogo()
    paginaAtual.value = 1
    carregarProdutos()
  } catch (error) {
    const mensagemErro = error.response?.data?.mensagem || 'Erro ao salvar produto'
    mostrarMensagem('error', mensagemErro)
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const confirmarDelecao = (produto) => {
  produtoSelecionado.value = produto
  dialogoConfirmacao.value = true
}

const deletarProduto = async () => {
  try {
    carregando.value = true
    await produtoService.deletar(produtoSelecionado.value.id)
    mostrarMensagem('success', 'Produto deletado com sucesso!')
    dialogoConfirmacao.value = false
    carregarProdutos()
  } catch (error) {
    const mensagemErro = error.response?.data?.mensagem || 'Erro ao deletar produto'
    mostrarMensagem('error', mensagemErro)
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const formatarPreco = (preco) => {
  return parseFloat(preco).toFixed(2).replace('.', ',')
}

const mostrarMensagem = (tipo, texto) => {
  mensagem.value = { tipo, texto }
  setTimeout(() => {
    mensagem.value = { tipo: '', texto: '' }
  }, 5000)
}
</script>

<style scoped>
.produtos {
  padding: 20px 0;
}
</style>
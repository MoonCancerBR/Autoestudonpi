<template>
  <div class="categorias">
    <v-card class="mb-6">
      <v-card-title class="text-h5">Gerenciar Categorias</v-card-title>
      <v-card-text>
        <v-btn color="primary" @click="abrirDialogoNova" class="mb-4">
          <v-icon left>mdi-plus</v-icon>
          Nova Categoria
        </v-btn>

        <!-- Tabela de Categorias -->
        <v-table v-if="categorias.length > 0" class="mt-4">
          <thead>
            <tr>
              <th class="text-left">Nome</th>
              <th class="text-center">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="categoria in categorias" :key="categoria.id">
              <td>{{ categoria.nome }}</td>
              <td class="text-center">
                <v-btn size="small" color="warning" @click="editarCategoria(categoria)" class="mr-2" title="Editar">
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>
                <v-btn size="small" color="error" @click="confirmarDelecao(categoria)" title="Deletar">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </td>
            </tr>
          </tbody>
        </v-table>

        <v-alert v-else type="info" class="mt-4">
          Nenhuma categoria cadastrada. Clique em "Nova Categoria" para criar uma.
        </v-alert>

        <!-- Mensagens de Feedback -->
        <v-alert v-if="mensagem.tipo" :type="mensagem.tipo" class="mt-4" closable>
          {{ mensagem.texto }}
        </v-alert>
      </v-card-text>
    </v-card>

    <!-- Diálogo de Formulário -->
    <v-dialog v-model="dialogoAberto" max-width="500px">
      <v-card>
        <v-card-title>{{ editando ? 'Editar Categoria' : 'Nova Categoria' }}</v-card-title>
        <v-card-text>
          <v-form ref="formulario" @submit.prevent="salvarCategoria">
            <v-text-field
              v-model="formularioCategoria.nome"
              label="Nome da Categoria"
              :rules="regrasNome"
              required
              outlined
              class="mb-4"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" @click="fecharDialogo">Cancelar</v-btn>
          <v-btn color="primary" @click="salvarCategoria" :loading="carregando">
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
          Tem certeza que deseja deletar a categoria "{{ categoriaSelecionada?.nome }}"?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" @click="dialogoConfirmacao = false">Cancelar</v-btn>
          <v-btn color="error" @click="deletarCategoria" :loading="carregando">Deletar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { categoriaService } from '../services/api'

const categorias = ref([])
const dialogoAberto = ref(false)
const dialogoConfirmacao = ref(false)
const carregando = ref(false)
const editando = ref(false)
const formularioCategoria = ref({ nome: '' })
const categoriaSelecionada = ref(null)
const formulario = ref(null)
const mensagem = ref({ tipo: '', texto: '' })

const regrasNome = [
  v => !!v || 'Nome é obrigatório',
  v => (v && v.length > 0) || 'Nome não pode estar vazio'
]

onMounted(() => {
  carregarCategorias()
})

const carregarCategorias = async () => {
  try {
    carregando.value = true
    const response = await categoriaService.listarTodas()
    categorias.value = response.data
  } catch (error) {
    mostrarMensagem('error', 'Erro ao carregar categorias')
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const abrirDialogoNova = () => {
  editando.value = false
  formularioCategoria.value = { nome: '' }
  dialogoAberto.value = true
}

const editarCategoria = (categoria) => {
  editando.value = true
  categoriaSelecionada.value = categoria
  formularioCategoria.value = { ...categoria }
  dialogoAberto.value = true
}

const fecharDialogo = () => {
  dialogoAberto.value = false
  formularioCategoria.value = { nome: '' }
  categoriaSelecionada.value = null
}

const salvarCategoria = async () => {
  if (!formulario.value || !await formulario.value.validate()) {
    mostrarMensagem('error', 'Por favor, preencha todos os campos obrigatórios')
    return
  }

  try {
    carregando.value = true
    if (editando.value) {
      await categoriaService.atualizar(categoriaSelecionada.value.id, formularioCategoria.value)
      mostrarMensagem('success', 'Categoria atualizada com sucesso!')
    } else {
      await categoriaService.criar(formularioCategoria.value)
      mostrarMensagem('success', 'Categoria criada com sucesso!')
    }
    fecharDialogo()
    carregarCategorias()
  } catch (error) {
    const mensagemErro = error.response?.data?.mensagem || 'Erro ao salvar categoria'
    mostrarMensagem('error', mensagemErro)
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const confirmarDelecao = (categoria) => {
  categoriaSelecionada.value = categoria
  dialogoConfirmacao.value = true
}

const deletarCategoria = async () => {
  try {
    carregando.value = true
    await categoriaService.deletar(categoriaSelecionada.value.id)
    mostrarMensagem('success', 'Categoria deletada com sucesso!')
    dialogoConfirmacao.value = false
    carregarCategorias()
  } catch (error) {
    const mensagemErro = error.response?.data?.mensagem || 'Erro ao deletar categoria'
    mostrarMensagem('error', mensagemErro)
    console.error(error)
  } finally {
    carregando.value = false
  }
}

const mostrarMensagem = (tipo, texto) => {
  mensagem.value = { tipo, texto }
  setTimeout(() => {
    mensagem.value = { tipo: '', texto: '' }
  }, 5000)
}
</script>

<style scoped>
.categorias {
  padding: 20px 0;
}
</style>
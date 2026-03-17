package com.example.controller;

import com.example.dto.ProdutoDTO;
import com.example.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<Page<ProdutoDTO>> listarComPaginacao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProdutoDTO> produtos;
        
        if (nome != null && !nome.isEmpty()) {
            produtos = produtoService.buscarPorNome(nome, pageable);
        } else if (categoriaId != null) {
            produtos = produtoService.buscarPorCategoria(categoriaId, pageable);
        } else {
            produtos = produtoService.listarComPaginacao(pageable);
        }
        
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> obterPorId(@PathVariable Long id) {
        try {
            ProdutoDTO produto = produtoService.obterPorId(id);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody ProdutoDTO produtoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensagens = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro de validação", mensagens));
        }

        try {
            ProdutoDTO produtoCriado = produtoService.criar(produtoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao criar produto", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensagens = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro de validação", mensagens));
        }

        try {
            ProdutoDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao atualizar produto", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public static class ErrorResponse {
        public String erro;
        public String mensagem;

        public ErrorResponse(String erro, String mensagem) {
            this.erro = erro;
            this.mensagem = mensagem;
        }

        public String getErro() {
            return erro;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}

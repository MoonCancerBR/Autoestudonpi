package com.example.controller;

import com.example.dto.CategoriaDTO;
import com.example.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obterPorId(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoriaService.obterPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CategoriaDTO categoriaDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensagens = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro de validação", mensagens));
        }

        try {
            CategoriaDTO categoriaCriada = categoriaService.criar(categoriaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao criar categoria", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO categoriaDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String mensagens = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro de validação", mensagens));
        }

        try {
            CategoriaDTO categoriaAtualizada = categoriaService.atualizar(id, categoriaDTO);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Erro ao atualizar categoria", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            categoriaService.deletar(id);
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

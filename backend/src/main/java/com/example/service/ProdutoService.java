package com.example.service;

import com.example.dto.ProdutoDTO;
import com.example.entity.Categoria;
import com.example.entity.Produto;
import com.example.repository.CategoriaRepository;
import com.example.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public Page<ProdutoDTO> listarComPaginacao(Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findAll(pageable);
        List<ProdutoDTO> produtoDTOs = produtos.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(produtoDTOs, pageable, produtos.getTotalElements());
    }

    public Page<ProdutoDTO> buscarPorNome(String nome, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome, pageable);
        List<ProdutoDTO> produtoDTOs = produtos.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(produtoDTOs, pageable, produtos.getTotalElements());
    }

    public Page<ProdutoDTO> buscarPorCategoria(Long categoriaId, Pageable pageable) {
        Page<Produto> produtos = produtoRepository.findByCategoriaId(categoriaId, pageable);
        List<ProdutoDTO> produtoDTOs = produtos.getContent()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(produtoDTOs, pageable, produtos.getTotalElements());
    }

    public ProdutoDTO obterPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        return convertToDTO(produto);
    }

    public ProdutoDTO criar(ProdutoDTO produtoDTO) {
        // Categoria é opcional agora
        Categoria categoria = null;
        if (produtoDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(produtoDTO.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + produtoDTO.getCategoriaId()));
        }

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setQuantidade(produtoDTO.getQuantidade());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setCategoria(categoria);

        Produto produtoSalvo = produtoRepository.save(produto);
        return convertToDTO(produtoSalvo);
    }

    public ProdutoDTO atualizar(Long id, ProdutoDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        // Categoria é opcional agora
        Categoria categoria = null;
        if (produtoDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(produtoDTO.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + produtoDTO.getCategoriaId()));
        }

        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setQuantidade(produtoDTO.getQuantidade());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setCategoria(categoria);

        Produto produtoAtualizado = produtoRepository.save(produto);
        return convertToDTO(produtoAtualizado);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));
        produtoRepository.delete(produto);
    }

    private ProdutoDTO convertToDTO(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                produto.getQuantidade(),
                produto.getDescricao(),
                produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem categoria"
        );
    }
}

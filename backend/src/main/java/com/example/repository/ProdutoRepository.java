package com.example.repository;

import com.example.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findAll(Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Produto> findByCategoriaId(Long categoriaId, Pageable pageable);
}

package com.example.service;

import com.example.dto.CategoriaDTO;
import com.example.entity.Categoria;
import com.example.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO obterPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));
        return convertToDTO(categoria);
    }

    public CategoriaDTO criar(CategoriaDTO categoriaDTO) {
        if (categoriaRepository.findByNome(categoriaDTO.getNome()).isPresent()) {
            throw new IllegalArgumentException("Categoria com nome '" + categoriaDTO.getNome() + "' já existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(categoriaDTO.getNome());

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return convertToDTO(categoriaSalva);
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));

        if (categoriaRepository.existsByNomeAndIdNot(categoriaDTO.getNome(), id)) {
            throw new IllegalArgumentException("Categoria com nome '" + categoriaDTO.getNome() + "' já existe");
        }

        categoria.setNome(categoriaDTO.getNome());
        Categoria categoriaAtualizada = categoriaRepository.save(categoria);
        return convertToDTO(categoriaAtualizada);
    }

    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));
        categoriaRepository.delete(categoria);
    }

    private CategoriaDTO convertToDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }
}

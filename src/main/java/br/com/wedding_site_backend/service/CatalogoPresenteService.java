package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.CatalogoPresente;
import br.com.wedding_site_backend.dto.CatalogoPresenteDTO;
import br.com.wedding_site_backend.repository.CatalogoPresenteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoPresenteService {

    private final CatalogoPresenteRepository repository;

    public List<CatalogoPresenteDTO> listarTodos() {
        return repository.findByIdGreaterThan(0L)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CatalogoPresenteDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public CatalogoPresenteDTO criar(CatalogoPresenteDTO dto) {
        CatalogoPresente presente = CatalogoPresente.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .imagemUrl(dto.getImagemUrl())
                .criadoEm(LocalDateTime.now())
                .build();
        return toDTO(repository.save(presente));
    }

    @Transactional
    public CatalogoPresenteDTO atualizar(Long id, CatalogoPresenteDTO dto) {
        CatalogoPresente presente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presente não encontrado com ID: " + id));

        presente.setNome(dto.getNome());
        presente.setDescricao(dto.getDescricao());
        presente.setValor(dto.getValor());
        presente.setImagemUrl(dto.getImagemUrl());

        return toDTO(repository.save(presente));
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
    }

    // ── Helpers ───────────────────────────────────────────
    public CatalogoPresente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Presente não encontrado: " + id));
    }

    public CatalogoPresenteDTO toDTO(CatalogoPresente p) {
        return CatalogoPresenteDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .descricao(p.getDescricao())
                .valor(p.getValor())
                .imagemUrl(p.getImagemUrl())
                .criadoEm(p.getCriadoEm())
                .build();
    }
}
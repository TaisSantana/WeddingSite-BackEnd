package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.CatalogoPresenteDTO;
import br.com.wedding_site_backend.service.CatalogoPresenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presentes")
@RequiredArgsConstructor
public class CatalogoPresenteController {

    private final CatalogoPresenteService service;

    @GetMapping
    public List<CatalogoPresenteDTO> listar() {
        return service.listarTodos();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CatalogoPresenteDTO> criar(@RequestBody @Valid CatalogoPresenteDTO dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
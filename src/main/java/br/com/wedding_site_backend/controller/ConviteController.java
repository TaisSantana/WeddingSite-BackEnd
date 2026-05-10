package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.ConviteCreateDTO;
import br.com.wedding_site_backend.dto.ConviteResponseDTO;
import br.com.wedding_site_backend.dto.RsvpItemDTO;
import br.com.wedding_site_backend.service.ConviteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convites")
@RequiredArgsConstructor
public class ConviteController {

    private final ConviteService service;

    /** Público — convidado busca pelo código do convite */
    @GetMapping("/{codigo}")
    public ResponseEntity<ConviteResponseDTO> buscar(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    /** Público — convidado confirma presença */
    @PostMapping("/{codigo}/confirmar")
    public ResponseEntity<Void> confirmar(
            @PathVariable String codigo,
            @RequestBody @Valid List<RsvpItemDTO> rsvps) {
        service.confirmarPresencas(codigo, rsvps);
        return ResponseEntity.ok().build();
    }

    /** Admin — listar todos os convites */
    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public List<ConviteResponseDTO> listar() {
        return service.listarTodos();
    }

    /** Admin — criar novo convite */
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConviteResponseDTO> criar(
            @RequestBody @Valid ConviteCreateDTO dto) {
        return ResponseEntity.status(201).body(service.criar(dto));
    }

    /** Admin — remover convite */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String codigo) {
        service.deletar(codigo);
        return ResponseEntity.noContent().build();
    }
}
package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.ConvidadoDTO;
import br.com.wedding_site_backend.dto.ConviteDTO;
import br.com.wedding_site_backend.dto.ConviteResponseDTO;
import br.com.wedding_site_backend.dto.RsvpItemDTO;
import br.com.wedding_site_backend.service.ConvidadoService;
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

    private final ConvidadoService convidadoService;
    private final ConviteService service;

    @GetMapping("/{codigo}")
    public ResponseEntity<ConviteResponseDTO> buscarconvite(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarConvitePorCodigo(codigo));
    }

    @PostMapping("/{codigo}/confirmar")
    public ResponseEntity<Void> confirmarpresenca(
            @PathVariable String codigo,
            @RequestBody @Valid List<RsvpItemDTO> rsvps) {
        service.confirmarPresencas(codigo, rsvps);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ConviteResponseDTO> listar() {
        return service.listarTodosConvites();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConviteResponseDTO> criarconvite(
            @RequestBody @Valid ConviteDTO dto) {
        return ResponseEntity.status(201).body(service.criarConvite(dto));
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarconvite(@PathVariable String codigo) {
        service.deletarConvite(codigo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConviteResponseDTO> editarconvite(
            @PathVariable String codigo,
            @RequestBody ConviteDTO dto) {

        return ResponseEntity.ok(service.editarConvite(codigo, dto));
    }

    @PostMapping("/convidados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConvidadoDTO> criarConvidado(
            @RequestBody @Valid ConvidadoDTO dto) {

        return ResponseEntity.status(201).body(convidadoService.criarConvidado(dto));
    }

    @PatchMapping("/convidados/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> atualizarConvidado(
            @PathVariable Long id,
            @RequestBody ConvidadoDTO dto) {

        convidadoService.atualizarConvidado(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/convidados/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarConvidado(@PathVariable Long id) {

        convidadoService.deletarConvidado(id);
        return ResponseEntity.noContent().build();
    }


}
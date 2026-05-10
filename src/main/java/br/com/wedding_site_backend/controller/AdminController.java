package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.*;
import br.com.wedding_site_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDTO> login(@RequestBody @Valid AdminLoginDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsDTO> stats() {
        return ResponseEntity.ok(service.stats());
    }

    // Lista todos os presentes recebidos com detalhes dos itens e mensagens
    @GetMapping("/presentes-recebidos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PresenteRecebidoDTO> presentesRecebidos() {
        return service.listarPresentesRecebidos();
    }
}
package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.PresenteRecebidoDTO;
import br.com.wedding_site_backend.service.PresenteRecebidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/presentes-recebidos")
@RequiredArgsConstructor
public class PresenteRecebidoController {

    private final PresenteRecebidoService service;

    // Admin — lista todos com itens e mensagens
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<PresenteRecebidoDTO> listar() {
//        return service.listarTodos();
//    }

    // Admin — só os pagos
    @GetMapping("/pagos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PresenteRecebidoDTO> listarPagos() {
        return service.listarPagos();
    }
}
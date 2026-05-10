package br.com.wedding_site_backend.service;


import br.com.wedding_site_backend.domain.Convidado;
import br.com.wedding_site_backend.domain.Convite;
import br.com.wedding_site_backend.dto.*;
import br.com.wedding_site_backend.enums.StatusPresenca;
import br.com.wedding_site_backend.repository.ConvidadoRepository;
import br.com.wedding_site_backend.repository.ConviteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConviteService {

    private final ConviteRepository conviteRepo;
    private final ConvidadoRepository convidadoRepo;

    public ConviteResponseDTO buscarPorCodigo(String codigo) {
        Convite c = conviteRepo.findByCodigo(codigo.toUpperCase())
            .orElseThrow(() -> new EntityNotFoundException("Código não encontrado: " + codigo));
        return toDTO(c);
    }

    @Transactional
    public void confirmarPresencas(String codigo, List<RsvpItemDTO> rsvps) {
        conviteRepo.findByCodigo(codigo.toUpperCase())
            .orElseThrow(() -> new EntityNotFoundException("Código não encontrado: " + codigo));

        rsvps.forEach(rsvp -> {
            Convidado g = convidadoRepo.findById(rsvp.getConvidadoId())
                .orElseThrow(() -> new EntityNotFoundException("Convidado não encontrado: " + rsvp.getConvidadoId()));
            g.setStatus(rsvp.getStatus());
            convidadoRepo.save(g);
        });
    }

    @Transactional
    public ConviteResponseDTO criar(ConviteCreateDTO dto) {
        if (conviteRepo.existsByCodigo(dto.getCodigo().toUpperCase())) {
            throw new IllegalArgumentException("Código já existe: " + dto.getCodigo());
        }
        Convite convite = Convite.builder()
            .codigo(dto.getCodigo().toUpperCase())
            .familia(dto.getFamilia())
            .build();

        dto.getConvidados().forEach(nome -> {
            Convidado g = Convidado.builder()
                .convite(convite)
                .nome(nome.trim())
                .status(StatusPresenca.PENDENTE)
                .build();
            convite.getConvidados().add(g);
        });

        return toDTO(conviteRepo.save(convite));
    }

    @Transactional
    public void deletar(String codigo) {
        conviteRepo.deleteById(codigo);
    }

    public List<ConviteResponseDTO> listarTodos() {
        return conviteRepo.findAll().stream().map(this::toDTO).toList();
    }

    // ── Estatísticas para o admin ────────────────────────
    public AdminStatsDTO.ConvidadoStats statsConvidados() {
        long confirmados = convidadoRepo.countByStatus("CONFIRMADO");
        long pendentes   = convidadoRepo.countByStatus("PENDENTE");
        long talvez      = convidadoRepo.countByStatus("TALVEZ");
        long naoVem      = convidadoRepo.countByStatus("NAO_VEM");
        return new AdminStatsDTO.ConvidadoStats(confirmados, pendentes, talvez, naoVem);
    }

    private ConviteResponseDTO toDTO(Convite c) {
        List<ConvidadoDTO> convidados = c.getConvidados().stream()
            .map(g -> ConvidadoDTO.builder()
                .id(g.getId()).nome(g.getNome()).status(g.getStatus()).build())
            .toList();
        return ConviteResponseDTO.builder()
            .codigo(c.getCodigo()).familia(c.getFamilia())
            .convidados(convidados).build();
    }
}
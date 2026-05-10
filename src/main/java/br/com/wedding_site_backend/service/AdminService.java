package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.PresenteRecebido;
import br.com.wedding_site_backend.dto.*;
import br.com.wedding_site_backend.repository.*;
import br.com.wedding_site_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final JwtService                 jwtService;
    private final PasswordEncoder            passwordEncoder;
    private final CatalogoPresenteRepository catalogoRepo;
    private final PresenteRecebidoRepository presenteRecebidoRepo;
    private final ConviteService             conviteService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password-hash}")
    private String adminPasswordHash;

    public AdminLoginResponseDTO login(AdminLoginDTO dto) {
        if (!adminUsername.equals(dto.getUsername())
                || !passwordEncoder.matches(dto.getPassword(), adminPasswordHash)) {
            throw new IllegalArgumentException("Credenciais invalidas.");
        }
        return AdminLoginResponseDTO.builder()
                .token(jwtService.gerarToken(adminUsername))
                .expiresIn(86400L)
                .build();
    }

    public AdminStatsDTO stats() {
        long totalCatalogo   = catalogoRepo.count();
        long totalPresentes  = presenteRecebidoRepo.count();
        BigDecimal totalPago = presenteRecebidoRepo.somarTotalPago();
        AdminStatsDTO.ConvidadoStats cs = conviteService.statsConvidados();

        return AdminStatsDTO.builder()
                .totalPresentes(totalCatalogo)
                .presentesReservados(totalPresentes)
                .totalContribuicoes(totalPresentes)
                .valorTotal(totalPago != null ? totalPago : BigDecimal.ZERO)
                .confirmados(cs.getConfirmados())
                .pendentes(cs.getPendentes())
                .talvez(cs.getTalvez())
                .naoVem(cs.getNaoVem())
                .build();
    }

    public List<PresenteRecebidoDTO> listarPresentesRecebidos() {
        return presenteRecebidoRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private PresenteRecebidoDTO toDTO(PresenteRecebido pr) {
        List<ItemPresenteRecebidoDTO> itens = pr.getItens().stream()
                .map(item -> ItemPresenteRecebidoDTO.builder()
                        .catalogoId(item.getCatalogoPresente().getId())
                        .nomePresente(item.getCatalogoPresente().getNome())
                        .valorPago(item.getValorPago())
                        .build())
                .toList();

        return PresenteRecebidoDTO.builder()
                .id(pr.getId())
                .nomeDoador(pr.getNomeDoador())
                .emailDoador(pr.getEmailDoador())
                .mensagem(pr.getMensagem())
                .formaPagamento(pr.getFormaPagamento())
                .total(pr.getTotal())
                .statusPagamento(pr.getStatusPagamento())
                .criadoEm(pr.getCriadoEm())
                .pagoEm(pr.getPagoEm())
                .itens(itens)
                .build();
    }
}



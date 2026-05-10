package br.com.wedding_site_backend.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagamentoResponseDTO {
    private Long presenteRecebidoId;
    private String preferenceId;   // ID da preferência MP
    private String checkoutUrl;    // URL para redirecionar o usuário
    private String sandboxUrl;     // URL sandbox (testes)
}

package br.com.wedding_site_backend.dto;

import lombok.*;
import java.math.BigDecimal;

// ── Resposta do Pix ───────────────────────────────────────
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PixResponseDTO {
    private Long presenteRecebidoId;
    private String mpPaymentId;
    private BigDecimal valor;
    private String copiaECola;       // código copia e cola
    private String qrCodeBase64;     // imagem QR em base64
    private String expiresAt;
    private String status;
}
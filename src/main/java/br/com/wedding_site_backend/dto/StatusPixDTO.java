package br.com.wedding_site_backend.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusPixDTO {
    private String paymentId;
    // PENDENTE | PAGO | EXPIRADO
    private String status;
    private String pagoEm;
}

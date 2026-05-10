package br.com.wedding_site_backend.dto;

import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPresenteRecebidoDTO {

    private Long catalogoId;
    private String nomePresente;
    private BigDecimal valorPago;
}

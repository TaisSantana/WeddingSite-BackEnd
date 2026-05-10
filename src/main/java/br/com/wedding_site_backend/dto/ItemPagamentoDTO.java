package br.com.wedding_site_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemPagamentoDTO {
    @NotNull private Long presenteId;
}
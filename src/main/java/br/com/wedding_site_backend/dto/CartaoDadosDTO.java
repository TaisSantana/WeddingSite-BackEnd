package br.com.wedding_site_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CartaoDadosDTO {
    @NotBlank private String numero;
    @NotBlank private String nomeTitular;
    @NotBlank private String validade;
    @NotBlank private String cvv;
    @Min(1) @Max(12) private int parcelas;
}
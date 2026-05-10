package br.com.wedding_site_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
// ── Catálogo de Presentes ─────────────────────────────────
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CatalogoPresenteDTO {
 
    private Long id;
 
    @NotBlank
    private String nome;
 
    private String descricao;
 
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;
 
    private String imagemUrl;
    private LocalDateTime criadoEm;
}
 
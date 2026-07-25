package br.com.wedding_site_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagamentoRequestDTO {

    @NotBlank private String nome;
    @NotBlank @Email private String email;
    private String mensagem;
    @NotEmpty @Valid
    private List<ItemCarrinhoDTO> itens;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ItemCarrinhoDTO {
        @NotNull private Long catalogoId;
        // Usado apenas quando catalogoId == -1 (Loot Misterioso da Guilda).
        // Para os demais itens, o valor real vem sempre do catalogo no banco,
        // nunca deste campo — validação de faixa é feita no service.
        @DecimalMin(value = "0.0", inclusive = false)
        @DecimalMax(value = "50000.0")
        private BigDecimal valorPersonalizado;
    }
}
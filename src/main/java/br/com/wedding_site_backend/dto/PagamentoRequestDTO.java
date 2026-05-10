package br.com.wedding_site_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

// ── Request compartilhado (Pix e Checkout Pro) ────────────
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagamentoRequestDTO {

    @NotBlank
    private String nomeDoador;

    @NotBlank @Email
    private String emailDoador;

    private String mensagem;

    @NotEmpty @Valid
    private List<ItemCarrinhoDTO> itens;

    // ── Item do carrinho ──────────────────────────────────
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ItemCarrinhoDTO {
        @NotNull
        private Long catalogoId;
    }
}
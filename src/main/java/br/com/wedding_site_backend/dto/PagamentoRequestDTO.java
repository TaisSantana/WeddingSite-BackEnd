package br.com.wedding_site_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    }
}
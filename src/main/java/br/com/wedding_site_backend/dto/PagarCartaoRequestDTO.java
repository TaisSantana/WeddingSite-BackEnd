package br.com.wedding_site_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagarCartaoRequestDTO {
    @NotBlank private String nomeDoador;
    @NotBlank @Email private String emailDoador;
    private String mensagem;
    @NotEmpty @Valid private List<ItemPagamentoDTO> itens;
    @NotNull @Valid private CartaoDadosDTO cartao;

//    public IniciarPixRequestDTO toPixRequest() {
//        return IniciarPixRequestDTO.builder()
//            .nomeDoador(nomeDoador).emailDoador(emailDoador)
//            .mensagem(mensagem).itens(itens).build();
//    }
}

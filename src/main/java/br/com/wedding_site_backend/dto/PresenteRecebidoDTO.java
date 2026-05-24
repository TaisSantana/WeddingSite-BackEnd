package br.com.wedding_site_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresenteRecebidoDTO {

    private Long id;
    private String nome;
    private String email;
    private String mensagem;
    private String formaPagamento;
    private BigDecimal total;
    private String statusPagamento;
    private LocalDateTime criadoEm;
    private LocalDateTime pagoEm;

    // Itens escolhidos no carrinho
    private List<ItemPresenteRecebidoDTO> itens;
}

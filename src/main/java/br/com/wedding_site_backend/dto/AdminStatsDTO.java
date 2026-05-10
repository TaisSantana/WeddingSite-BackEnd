package br.com.wedding_site_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsDTO {
    private long totalPresentes;
    private long presentesReservados;
    private long totalContribuicoes;
    private BigDecimal valorTotal;
    private long confirmados;
    private long pendentes;
    private long talvez;
    private long naoVem;

    @Data @AllArgsConstructor
    public static class ConvidadoStats {
        private long confirmados;
        private long pendentes;
        private long talvez;
        private long naoVem;
    }
}
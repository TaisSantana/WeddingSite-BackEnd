package br.com.wedding_site_backend.dto;

import br.com.wedding_site_backend.enums.StatusPresenca;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConvidadoDTO {
    private Long id;
    private String nome;
    private StatusPresenca status;
}
package br.com.wedding_site_backend.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConviteResponseDTO {
    private Long id;
    private String codigo;
    private String familia;
    private List<ConvidadoDTO> convidados;
}
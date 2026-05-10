package br.com.wedding_site_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PagarCartaoResponseDTO {
    private String id;
    private String status;
    private String mensagem;
    private String redirectUrl;
}
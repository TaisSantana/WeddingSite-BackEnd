package br.com.wedding_site_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ConviteDTO {
    private String codigo;
    @NotBlank private String familia;
    @NotEmpty private List<String> convidados;
}
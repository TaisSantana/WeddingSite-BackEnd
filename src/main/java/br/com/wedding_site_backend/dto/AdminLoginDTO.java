package br.com.wedding_site_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AdminLoginDTO {
    @NotBlank private String username;
    @NotBlank private String password;
}
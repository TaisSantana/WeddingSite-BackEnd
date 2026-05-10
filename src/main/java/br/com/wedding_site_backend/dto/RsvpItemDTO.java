package br.com.wedding_site_backend.dto;

import br.com.wedding_site_backend.enums.StatusPresenca;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RsvpItemDTO {
    @NotNull
    private Long convidadoId;
    @NotNull
    private StatusPresenca status;
}
package br.com.metas.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de acesso")
public record LoginRequest(
        @Schema(example = "maria@email.com")
        @NotBlank @Email String email,

        @Schema(example = "senha123")
        @NotBlank String senha
) {
}

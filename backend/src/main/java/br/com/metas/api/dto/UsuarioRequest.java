package br.com.metas.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou alteração de usuário")
public record UsuarioRequest(
        @Schema(example = "Maria Silva")
        @NotBlank @Size(max = 100) String nome,

        @Schema(example = "maria@email.com")
        @NotBlank @Email @Size(max = 150) String email,

        @Schema(example = "senha123")
        @NotBlank @Size(min = 6, max = 60) String senha
) {
}

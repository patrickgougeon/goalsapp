package br.com.metas.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou alteração de item do checklist")
public record ItemChecklistRequest(
        @Schema(example = "Correr 2 km na segunda-feira")
        @NotBlank @Size(max = 200) String descricao,

        @Schema(description = "Posição do item na lista; se omitido, o item vai para o final", example = "1")
        Integer ordem,

        @Schema(example = "false")
        Boolean concluido
) {
}

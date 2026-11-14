package br.com.metas.api.dto;

import br.com.metas.api.entity.StatusMeta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @Schema(example = "CONCLUIDA")
        @NotNull StatusMeta status
) {
}

package br.com.metas.api.dto;

import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.Prioridade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Dados para cadastro ou alteração de meta")
public record MetaRequest(
        @Schema(example = "Correr 5 km")
        @NotBlank @Size(max = 120) String titulo,

        @Schema(example = "Conseguir correr 5 km sem parar até o fim do mês")
        @Size(max = 500) String descricao,

        @Schema(example = "SAUDE")
        @NotNull Categoria categoria,

        @Schema(example = "ALTA")
        @NotNull Prioridade prioridade,

        @Schema(example = "2026-12-31")
        LocalDate dataLimite,

        @Schema(description = "Itens iniciais do checklist (considerado apenas no cadastro)")
        List<@Valid ItemChecklistRequest> itens
) {
}

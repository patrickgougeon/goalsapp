package br.com.metas.api.dto;

import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.Meta;
import br.com.metas.api.entity.Prioridade;
import br.com.metas.api.entity.StatusMeta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MetaResponse(
        Long id,
        Long usuarioId,
        String titulo,
        String descricao,
        Categoria categoria,
        Prioridade prioridade,
        StatusMeta status,
        LocalDate dataLimite,
        LocalDateTime criadaEm,
        LocalDateTime concluidaEm,
        @Schema(description = "Percentual de itens concluídos (0 a 100)") int progresso,
        boolean atrasada,
        int totalItens,
        int itensConcluidos,
        List<ItemChecklistResponse> itens
) {

    public static MetaResponse de(Meta meta) {
        List<ItemChecklistResponse> itens = meta.getItens().stream().map(ItemChecklistResponse::de).toList();
        int concluidos = (int) itens.stream().filter(ItemChecklistResponse::concluido).count();
        return new MetaResponse(
                meta.getId(),
                meta.getUsuario().getId(),
                meta.getTitulo(),
                meta.getDescricao(),
                meta.getCategoria(),
                meta.getPrioridade(),
                meta.getStatus(),
                meta.getDataLimite(),
                meta.getCriadaEm(),
                meta.getConcluidaEm(),
                meta.calcularProgresso(),
                meta.isAtrasada(),
                itens.size(),
                concluidos,
                itens
        );
    }
}

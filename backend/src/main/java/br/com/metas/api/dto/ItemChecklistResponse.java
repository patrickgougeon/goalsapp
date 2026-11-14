package br.com.metas.api.dto;

import br.com.metas.api.entity.ItemChecklist;

import java.time.LocalDateTime;

public record ItemChecklistResponse(Long id, Long metaId, String descricao, boolean concluido, int ordem,
                                    LocalDateTime concluidoEm) {

    public static ItemChecklistResponse de(ItemChecklist item) {
        return new ItemChecklistResponse(item.getId(), item.getMeta().getId(), item.getDescricao(),
                item.isConcluido(), item.getOrdem(), item.getConcluidoEm());
    }
}

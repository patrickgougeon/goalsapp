package br.com.metas.api.service;

import br.com.metas.api.dto.ItemChecklistRequest;
import br.com.metas.api.dto.ItemChecklistResponse;
import br.com.metas.api.entity.ItemChecklist;
import br.com.metas.api.entity.Meta;
import br.com.metas.api.entity.StatusMeta;
import br.com.metas.api.exception.RecursoNaoEncontradoException;
import br.com.metas.api.exception.RegraNegocioException;
import br.com.metas.api.repository.ItemChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemChecklistService {

    private final ItemChecklistRepository itemRepository;
    private final MetaService metaService;

    @Transactional(readOnly = true)
    public List<ItemChecklistResponse> listarPorMeta(Long metaId) {
        return metaService.buscarEntidade(metaId).getItens().stream()
                .map(ItemChecklistResponse::de)
                .toList();
    }

    @Transactional
    public ItemChecklistResponse adicionar(Long metaId, ItemChecklistRequest request) {
        Meta meta = metaService.buscarEntidade(metaId);
        validarMetaEditavel(meta);

        ItemChecklist item = new ItemChecklist();
        item.setDescricao(request.descricao().trim());
        item.setOrdem(request.ordem() != null ? request.ordem() : meta.getItens().size() + 1);
        item.marcarConcluido(Boolean.TRUE.equals(request.concluido()));
        meta.adicionarItem(item);
        meta.sincronizarStatusComChecklist();

        return ItemChecklistResponse.de(itemRepository.save(item));
    }

    @Transactional
    public ItemChecklistResponse atualizar(Long id, ItemChecklistRequest request) {
        ItemChecklist item = buscarEntidade(id);
        validarMetaEditavel(item.getMeta());

        item.setDescricao(request.descricao().trim());
        if (request.ordem() != null) {
            item.setOrdem(request.ordem());
        }
        if (request.concluido() != null && request.concluido() != item.isConcluido()) {
            item.marcarConcluido(request.concluido());
        }
        item.getMeta().sincronizarStatusComChecklist();
        return ItemChecklistResponse.de(item);
    }

    @Transactional
    public ItemChecklistResponse alternarConclusao(Long id) {
        ItemChecklist item = buscarEntidade(id);
        validarMetaEditavel(item.getMeta());

        item.marcarConcluido(!item.isConcluido());
        item.getMeta().sincronizarStatusComChecklist();
        return ItemChecklistResponse.de(item);
    }

    @Transactional
    public void remover(Long id) {
        ItemChecklist item = buscarEntidade(id);
        Meta meta = item.getMeta();
        validarMetaEditavel(meta);

        meta.getItens().remove(item);
        meta.sincronizarStatusComChecklist();
    }

    private ItemChecklist buscarEntidade(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do checklist", id));
    }

    private void validarMetaEditavel(Meta meta) {
        if (meta.getStatus() == StatusMeta.CANCELADA) {
            throw new RegraNegocioException("Não é possível alterar o checklist de uma meta cancelada");
        }
    }
}

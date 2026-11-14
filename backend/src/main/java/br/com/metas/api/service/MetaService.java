package br.com.metas.api.service;

import br.com.metas.api.dto.ItemChecklistRequest;
import br.com.metas.api.dto.MetaRequest;
import br.com.metas.api.dto.MetaResponse;
import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.ItemChecklist;
import br.com.metas.api.entity.Meta;
import br.com.metas.api.entity.StatusMeta;
import br.com.metas.api.exception.RecursoNaoEncontradoException;
import br.com.metas.api.repository.MetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<MetaResponse> listarPorUsuario(Long usuarioId, StatusMeta status, Categoria categoria) {
        usuarioService.buscarEntidade(usuarioId);
        return metaRepository.buscarPorUsuario(usuarioId, status, categoria).stream()
                .map(MetaResponse::de)
                .toList();
    }

    @Transactional
    public MetaResponse cadastrar(Long usuarioId, MetaRequest request) {
        Meta meta = new Meta();
        meta.setUsuario(usuarioService.buscarEntidade(usuarioId));
        preencher(meta, request);

        List<ItemChecklistRequest> itens = request.itens() == null ? List.of() : request.itens();
        for (int i = 0; i < itens.size(); i++) {
            ItemChecklistRequest itemRequest = itens.get(i);
            ItemChecklist item = new ItemChecklist();
            item.setDescricao(itemRequest.descricao().trim());
            item.setOrdem(itemRequest.ordem() != null ? itemRequest.ordem() : i + 1);
            item.marcarConcluido(Boolean.TRUE.equals(itemRequest.concluido()));
            meta.adicionarItem(item);
        }
        meta.sincronizarStatusComChecklist();

        return MetaResponse.de(metaRepository.save(meta));
    }

    @Transactional(readOnly = true)
    public MetaResponse buscar(Long id) {
        return MetaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public MetaResponse atualizar(Long id, MetaRequest request) {
        Meta meta = buscarEntidade(id);
        preencher(meta, request);
        return MetaResponse.de(meta);
    }

    @Transactional
    public MetaResponse alterarStatus(Long id, StatusMeta status) {
        Meta meta = buscarEntidade(id);
        meta.alterarStatus(status);
        return MetaResponse.de(meta);
    }

    @Transactional
    public void remover(Long id) {
        metaRepository.delete(buscarEntidade(id));
    }

    Meta buscarEntidade(Long id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meta", id));
    }

    private void preencher(Meta meta, MetaRequest request) {
        meta.setTitulo(request.titulo().trim());
        meta.setDescricao(request.descricao());
        meta.setCategoria(request.categoria());
        meta.setPrioridade(request.prioridade());
        meta.setDataLimite(request.dataLimite());
    }
}

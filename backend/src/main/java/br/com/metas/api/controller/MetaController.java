package br.com.metas.api.controller;

import br.com.metas.api.dto.MetaRequest;
import br.com.metas.api.dto.MetaResponse;
import br.com.metas.api.dto.StatusRequest;
import br.com.metas.api.entity.Categoria;
import br.com.metas.api.entity.StatusMeta;
import br.com.metas.api.service.MetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Metas", description = "Gerenciamento das metas do usuário")
public class MetaController {

    private final MetaService metaService;

    @GetMapping("/usuarios/{usuarioId}/metas")
    @Operation(summary = "Lista as metas de um usuário", description = "Permite filtrar por status e categoria.")
    public List<MetaResponse> listar(@PathVariable Long usuarioId,
                                     @RequestParam(required = false) StatusMeta status,
                                     @RequestParam(required = false) Categoria categoria) {
        return metaService.listarPorUsuario(usuarioId, status, categoria);
    }

    @PostMapping("/usuarios/{usuarioId}/metas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma meta para o usuário", description = "Pode receber os itens iniciais do checklist.")
    public MetaResponse cadastrar(@PathVariable Long usuarioId, @RequestBody @Valid MetaRequest request) {
        return metaService.cadastrar(usuarioId, request);
    }

    @GetMapping("/metas/{id}")
    @Operation(summary = "Consulta uma meta com seu checklist e progresso")
    public MetaResponse buscar(@PathVariable Long id) {
        return metaService.buscar(id);
    }

    @PutMapping("/metas/{id}")
    @Operation(summary = "Altera os dados de uma meta", description = "Os itens do checklist são gerenciados pelos endpoints de itens.")
    public MetaResponse atualizar(@PathVariable Long id, @RequestBody @Valid MetaRequest request) {
        return metaService.atualizar(id, request);
    }

    @PatchMapping("/metas/{id}/status")
    @Operation(summary = "Altera o status de uma meta (EM_ANDAMENTO, CONCLUIDA ou CANCELADA)")
    public MetaResponse alterarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest request) {
        return metaService.alterarStatus(id, request.status());
    }

    @DeleteMapping("/metas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma meta e seu checklist")
    public void remover(@PathVariable Long id) {
        metaService.remover(id);
    }
}

package br.com.metas.api.controller;

import br.com.metas.api.dto.ItemChecklistRequest;
import br.com.metas.api.dto.ItemChecklistResponse;
import br.com.metas.api.service.ItemChecklistService;
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
@Tag(name = "Checklist", description = "Itens do checklist de cada meta")
public class ItemChecklistController {

    private final ItemChecklistService itemService;

    @GetMapping("/metas/{metaId}/itens")
    @Operation(summary = "Lista os itens do checklist de uma meta")
    public List<ItemChecklistResponse> listar(@PathVariable Long metaId) {
        return itemService.listarPorMeta(metaId);
    }

    @PostMapping("/metas/{metaId}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona um item ao checklist de uma meta")
    public ItemChecklistResponse adicionar(@PathVariable Long metaId, @RequestBody @Valid ItemChecklistRequest request) {
        return itemService.adicionar(metaId, request);
    }

    @PutMapping("/itens/{id}")
    @Operation(summary = "Altera um item do checklist")
    public ItemChecklistResponse atualizar(@PathVariable Long id, @RequestBody @Valid ItemChecklistRequest request) {
        return itemService.atualizar(id, request);
    }

    @PatchMapping("/itens/{id}/alternar")
    @Operation(summary = "Marca/desmarca um item como concluído",
            description = "Quando todos os itens são concluídos, a meta é concluída automaticamente.")
    public ItemChecklistResponse alternar(@PathVariable Long id) {
        return itemService.alternarConclusao(id);
    }

    @DeleteMapping("/itens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um item do checklist")
    public void remover(@PathVariable Long id) {
        itemService.remover(id);
    }
}

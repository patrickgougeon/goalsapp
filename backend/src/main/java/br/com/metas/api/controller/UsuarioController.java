package br.com.metas.api.controller;

import br.com.metas.api.dto.DesempenhoResponse;
import br.com.metas.api.dto.LoginRequest;
import br.com.metas.api.dto.UsuarioRequest;
import br.com.metas.api.dto.UsuarioResponse;
import br.com.metas.api.service.DesempenhoService;
import br.com.metas.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Cadastro, autenticação e desempenho dos usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final DesempenhoService desempenhoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra um novo usuário")
    public UsuarioResponse cadastrar(@RequestBody @Valid UsuarioRequest request) {
        return usuarioService.cadastrar(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário por e-mail e senha")
    public UsuarioResponse login(@RequestBody @Valid LoginRequest request) {
        return usuarioService.autenticar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta um usuário pelo id")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera os dados de um usuário")
    public UsuarioResponse atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um usuário e todas as suas metas")
    public void remover(@PathVariable Long id) {
        usuarioService.remover(id);
    }

    @GetMapping("/{id}/desempenho")
    @Operation(summary = "Gera o resultado personalizado do usuário",
            description = "Analisa as metas e checklists do usuário e retorna estatísticas, pontuação (0-100), perfil e recomendações.")
    public DesempenhoResponse desempenho(@PathVariable Long id) {
        return desempenhoService.gerar(id);
    }
}

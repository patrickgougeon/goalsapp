package br.com.metas.api.service;

import br.com.metas.api.dto.LoginRequest;
import br.com.metas.api.dto.UsuarioRequest;
import br.com.metas.api.dto.UsuarioResponse;
import br.com.metas.api.entity.Usuario;
import br.com.metas.api.exception.CredenciaisInvalidasException;
import br.com.metas.api.exception.RecursoNaoEncontradoException;
import br.com.metas.api.exception.RegraNegocioException;
import br.com.metas.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
        }
        Usuario usuario = new Usuario();
        preencher(usuario, request);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse autenticar(LoginRequest request) {
        return usuarioRepository.findByEmailIgnoreCase(request.email())
                .filter(usuario -> passwordEncoder.matches(request.senha(), usuario.getSenhaHash()))
                .map(UsuarioResponse::de)
                .orElseThrow(CredenciaisInvalidasException::new);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.findByEmailIgnoreCase(request.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
                });
        preencher(usuario, request);
        return UsuarioResponse.de(usuario);
    }

    @Transactional
    public void remover(Long id) {
        usuarioRepository.delete(buscarEntidade(id));
    }

    Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private void preencher(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
    }
}

package br.com.metas.api.dto;

import br.com.metas.api.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(Long id, String nome, String email, LocalDateTime criadoEm) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCriadoEm());
    }
}

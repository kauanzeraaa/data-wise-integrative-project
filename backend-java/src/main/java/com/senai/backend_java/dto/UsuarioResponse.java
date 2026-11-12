package com.senai.backend_java.dto;

import java.time.LocalDateTime;

import com.senai.backend_java.models.Profile;
import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;

/** DTO de saída do usuário: nunca inclui senha nem hash. */
public record UsuarioResponse(Long id, String nome, String email, Profile perfil,
                              StatusUsuario status, LocalDateTime criadoEm) {
    /** Converte a entidade em DTO (método de fábrica usado pelos services/controllers). */
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(),
                u.getStatus(), u.getCriadoEm());
    }
}

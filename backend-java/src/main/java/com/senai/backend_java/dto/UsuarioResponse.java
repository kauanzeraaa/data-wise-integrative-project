package com.senai.backend_java.dto;

import com.senai.backend_java.models.Profile;
import com.senai.backend_java.models.Usuario;

// DTO de saída: nunca inclui senha nem hash.
public record UsuarioResponse(Long id, String nome, String email, Profile perfil) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
    }
}

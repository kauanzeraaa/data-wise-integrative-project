package com.senai.backend_java.repositories;

import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// O Spring Data JPA implementa os métodos de acesso ao banco.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {  // repositório de usuários (o Spring cria o SQL pelo nome do método)
    Optional<Usuario> findByEmailIgnoreCase(String email);                 // acha por e-mail, sem diferenciar maiúscula de minúscula
    boolean existsByEmailIgnoreCase(String email);                         // diz se o e-mail já está cadastrado
    List<Usuario> findAllByOrderByCriadoEmDesc();                          // todos os usuários, mais novos primeiro
    List<Usuario> findByStatusOrderByCriadoEmDesc(StatusUsuario status);   // só os de um status, mais novos primeiro
}
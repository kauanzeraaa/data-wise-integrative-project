package com.senai.backend_java.services;

import java.util.List;

import org.springframework.boot.security.autoconfigure.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.senai.backend_java.config.ApiException;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;

@Service                                                                            // serviço do Spring
public class UsuarioService {                                                       // regras de usuário: quem sou eu, listar, aprovar/bloquear
    private final UsuarioRepository repo;                                           // repositório de usuários

    public UsuarioService(UsuarioRepository repo) {                                 // construtor: o Spring entrega o repositório
        this.repo = repo;                                                           // guarda o repositório
    }                                                                               // fecha o bloco

    /** Usuário logado a partir do token JWT já validado pelo filtro. */
    public User atual(Authentication authentication) {                           // descobre quem está logado a partir do token
        return repo.findByEmailIgnoreCase(authentication.getName())                 // o e-mail do token vira busca no banco
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Sessão inválida. Entre novamente."));  // não achou: erro 401, entre de novo
    }                                                                               // fecha o bloco

    public List<UsuarioResponse> listar(StatusUsuario status) {                          // lista usuários para a tela de liberação
        List<User> lista = status == null ? repo.findAllByOrderByCriadoEmDesc()  // sem filtro: todos, mais novos primeiro
                : repo.findByStatusOrderByCriadoEmDesc(status);                     // com filtro: só os do status pedido
        return lista.stream().map(UsuarioD::de).toList();                         // converte cada Usuario em UsuarioDto (sem senha)
    }                                                                               // fecha o bloco

    public UsuarioResponse alterarStatus(Long id, Usuario quemAltera) {  // aprova, bloqueia ou deixa pendente um usuário
        Usuario alvo = repo.findById(id)                                            // busca o usuário alvo pelo id
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));  // não achou: erro 404
        if (alvo.getId().equals(quemAltera.getId())) {                              // o admin está mexendo em si mesmo?
            throw new ApiException(HttpStatus.BAD_REQUEST, "Você não pode alterar o seu próprio acesso.");  // então recusa com erro 400
        }                                                                           // fecha o bloco
        alvo.setStatus(novoStatus);                                                 // troca o status
        return UsuarioResponse.de(repo.save(alvo));                                      // salva no banco e devolve o DTO
    }                                                                               // fecha o bloco
}    
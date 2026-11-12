package com.senai.backend_java.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.senai.backend_java.config.ApiException;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;

/**
 * Regras de negócio sobre usuários: descobrir quem está logado,
 * listar usuários (tela de liberação) e aprovar/bloquear acessos.
 */
@Service
public class UsuarioService {
    private final UsuarioRepository repo;                                           // acesso à tabela "usuarios"

    public UsuarioService(UsuarioRepository repo) {                                 // injeção por construtor (o Spring entrega o repositório)
        this.repo = repo;
    }

    /**
     * Devolve a ENTIDADE do usuário logado.
     * O JwtAuthenticationFilter já validou o token e colocou o e-mail em authentication.getName().
     *
     * @throws ApiException 401 se o usuário do token não existe mais no banco
     */
    public Usuario atual(Authentication authentication) {
        return repo.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Sessão inválida. Entre novamente."));
    }

    /**
     * Lista usuários do mais novo para o mais antigo.
     *
     * @param status filtro opcional; null = todos
     * @return DTOs sem senha/hash (nunca devolva a entidade direto na API)
     */
    public List<UsuarioResponse> listar(StatusUsuario status) {
        List<Usuario> lista = status == null
                ? repo.findAllByOrderByCriadoEmDesc()
                : repo.findByStatusOrderByCriadoEmDesc(status);
        return lista.stream().map(UsuarioResponse::de).toList();
    }

    /**
     * Troca o status (PENDENTE / APROVADO / BLOQUEADO) de um usuário.
     *
     * @param id         id do usuário que terá o status alterado
     * @param novoStatus status desejado
     * @param quemAltera usuário logado (admin) que está fazendo a alteração
     * @throws ApiException 404 se o alvo não existe; 400 se o admin tentar alterar a si mesmo
     */
    public UsuarioResponse alterarStatus(Long id, StatusUsuario novoStatus, Usuario quemAltera) {
        if (novoStatus == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe o novo status.");
        }
        Usuario alvo = repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        if (alvo.getId().equals(quemAltera.getId())) {                              // impede o admin de se bloquear por engano
            throw new ApiException(HttpStatus.BAD_REQUEST, "Você não pode alterar o seu próprio acesso.");
        }
        alvo.setStatus(novoStatus);
        return UsuarioResponse.de(repo.save(alvo));
    }
}

package com.senai.backend_java.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Roda uma vez por requisição: lê o header "Authorization: Bearer &lt;token&gt;",
 * valida o JWT e, se estiver tudo certo, registra o usuário no SecurityContext
 * com a role "ROLE_USER" ou "ROLE_ADMIN". Sem token válido a requisição segue
 * anônima, e o SecurityConfig responde 401 nas rotas protegidas.
 */
@Component 
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UsuarioRepository repository;

    public JwtAuthenticationFilter(JwtService jwt, UsuarioRepository repository) {
        this.jwt = jwt;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith("Bearer ")) {
            String token = cabecalho.substring(7);
            try {
                String email = jwt.extrairEmailValidado(token); // Verifica assinatura e expiração.
                Usuario usuario = repository.findByEmailIgnoreCase(email).orElse(null);
                // só autentica quem existe E foi aprovado pelo admin; bloqueado/pendente perde o acesso na hora,
                // mesmo que ainda tenha um token válido guardado no navegador
                if (usuario != null && usuario.getStatus() == StatusUsuario.APROVADO) {
                    var autoridades = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()));
                    var autenticacao = new UsernamePasswordAuthenticationToken(email, null, autoridades);
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                }
            } catch (JwtException | IllegalArgumentException ex) {
                // Token inválido: não autentica; SecurityConfig devolverá 401 nas rotas privadas.
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
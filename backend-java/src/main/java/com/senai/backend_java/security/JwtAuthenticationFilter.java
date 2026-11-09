package com.senai.backend_java.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UserRepository;

@Component 
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserRepository repository;

    public JwtAuthenticationFilter(JwtService jwt, UserRepository repository) {
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
                if (usuario != null) {
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
package com.senai.backend_java.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.senai.backend_java.config.AppProperties;
import com.senai.backend_java.models.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Gera e valida os tokens JWT (assinatura HMAC-SHA256).
 *
 * <p>Configuração lida de AppProperties (application.properties):
 * <ul>
 *   <li>{@code app.jwt.secret} – chave em BASE64 com no mínimo 32 bytes (256 bits) aleatórios.
 *       Gere uma com: {@code openssl rand -base64 32}</li>
 *   <li>{@code app.jwt.expiration-minutes} – validade do token (padrão 60).</li>
 * </ul>
 */
@Service
public class JwtService {
    private final SecretKey chave;          // chave usada para assinar e conferir a assinatura
    private final long expiracaoMinutos;    // quanto tempo o token vale

    public JwtService(AppProperties props) {
        if (props.jwt() == null || props.jwt().secret() == null || props.jwt().secret().isBlank()) {
            // falha na subida do app: melhor do que rodar com uma chave vazia/insegura
            throw new IllegalStateException("Configure app.jwt.secret (Base64, mínimo 32 bytes).");
        }
        // Keys.hmacShaKeyFor lança WeakKeyException se a chave tiver menos de 256 bits
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.jwt().secret()));
        this.expiracaoMinutos = props.jwt().expirationMinutes() > 0 ? props.jwt().expirationMinutes() : 60;
    }

    /** Cria um token cujo "subject" é o e-mail do usuário. */
    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getEmail())                                    // identidade dentro do token
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(expiracaoMinutos * 60)))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Confere assinatura e validade e devolve o e-mail (subject).
     *
     * @throws io.jsonwebtoken.JwtException se o token for inválido, adulterado ou expirado
     */
    public String extrairEmailValidado(String token) {
        Claims claims = Jwts.parser().verifyWith(chave).build()
                .parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}

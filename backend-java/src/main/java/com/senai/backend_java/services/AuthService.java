package com.senai.backend_java.services;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.senai.backend_java.config.ApiException;
import com.senai.backend_java.dto.CadastroRequest;
import com.senai.backend_java.dto.LoginRequest;
import com.senai.backend_java.dto.LoginResponse;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.Profile;
import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;
import com.senai.backend_java.security.JwtService;

/**
 * Cadastro e login.
 * Fluxo: o usuário se cadastra (fica PENDENTE) -> o admin aprova (APROVADO) -> só então ele consegue entrar.
 */
@Service
public class AuthService {
  private final UsuarioRepository repository;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UsuarioRepository repository, PasswordEncoder encoder, JwtService jwt) {
    this.repository = repository;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  /**
   * Cria um usuário comum com status PENDENTE (aguardando aprovação do admin).
   *
   * @throws ApiException 400 se a senha passar de 72 bytes; 409 se o e-mail já existir
   */
  public UsuarioResponse cadastrar(CadastroRequest dados) {
    String email = dados.email().trim().toLowerCase(Locale.ROOT);   // e-mail sempre minúsculo: evita duplicar "Ana@x" e "ana@x"

    // BCrypt só considera os primeiros 72 BYTES. Letras acentuadas ocupam 2 bytes em UTF-8,
    // por isso o @Pattern (72 caracteres) sozinho não garante esse limite.
    if (dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Senha muito longa. Use no máximo 72 bytes.");
    }
    if (repository.existsByEmailIgnoreCase(email)) {
      throw new ApiException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
    }
    Usuario user = new Usuario();
    user.setNome(dados.nome().trim());
    user.setEmail(email);
    user.setSenhaHash(encoder.encode(dados.senha()));  // armazena apenas o hash, nunca a senha
    user.setPerfil(Profile.USER);                      // cadastro público nunca cria ADMIN
    user.setStatus(StatusUsuario.PENDENTE);            // precisa da aprovação do admin para entrar
    return UsuarioResponse.de(repository.save(user));
  }

  /**
   * Confere e-mail/senha e devolve um JWT.
   *
   * @throws ApiException 401 se e-mail ou senha estiverem errados; 403 se o acesso não estiver aprovado
   */
  public LoginResponse entrar(LoginRequest dados) {
    Usuario user = repository.findByEmailIgnoreCase(dados.email().trim()).orElse(null);

    // Mesma mensagem para "e-mail não existe" e "senha errada": não revela quais e-mails estão cadastrados.
    if (user == null || !encoder.matches(dados.senha(), user.getSenhaHash())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
    }

    // O status só é verificado DEPOIS da senha, para não informar nada a quem não sabe a senha.
    if (user.getStatus() == StatusUsuario.PENDENTE) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Seu cadastro ainda aguarda aprovação do administrador.");
    }
    if (user.getStatus() != StatusUsuario.APROVADO) {
      throw new ApiException(HttpStatus.FORBIDDEN, "Seu acesso está bloqueado. Procure o administrador.");
    }

    return new LoginResponse(jwt.gerar(user), "Bearer", UsuarioResponse.de(user));
  }
}

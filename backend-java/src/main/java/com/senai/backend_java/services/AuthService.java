package com.senai.backend_java.services;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.senai.backend_java.dto.CadastroRequest;
import com.senai.backend_java.dto.LoginRequest;
import com.senai.backend_java.dto.LoginResponse;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.Profile;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UserRepository;
import com.senai.backend_java.security.JwtService;

@Service
public class AuthService {
  private final UserRepository repository;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UserRepository repository, PasswordEncoder encoder, JwtService jwt){
    this.repository = repository;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public UsuarioResponse cadastrar(CadastroRequest dados){
    String email = dados.email().trim().toLowerCase(Locale.ROOT);
    
    // BCrypt considera no máximo 72 bytes; comprimento não equivale a bytes em UTF-8.
    if (dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password too long. Not accepted by BCypt.");
    }
    if (repository.existsByEmailIgnoreCase(email)){
      throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail already registered.");
    }
    Usuario user = new Usuario();
    user.setNome(dados.nome().trim());
    user.setEmail(email);
    user.setSenhaHash(encoder.encode(dados.senha())); // armazena apenas o Hash
    user.setPerfil(Profile.USER);
    return UsuarioResponse.de(repository.save(user));
  }

  public LoginResponse entrar(LoginRequest dados){
    Usuario user = repository.findByEmailIgnoreCase(dados.email().trim()).orElse(null);

    // mesmo erro para usuario inexitente ou senha invalida
    if (user == null || !encoder.matches(dados.senha(), user.getSenhaHash())){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail or password incorrect!");
    }

    return new LoginResponse(jwt.gerar(user), "Bearer", UsuarioResponse.de(user));
  }
}

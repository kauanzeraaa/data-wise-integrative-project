package com.senai.backend_java.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.senai.backend_java.dto.CadastroRequest;
import com.senai.backend_java.dto.LoginRequest;
import com.senai.backend_java.dto.LoginResponse;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UserRepository;
import com.senai.backend_java.services.AuthService;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final AuthService service;
  private final UserRepository repository;

  public AuthController(AuthService service, UserRepository repository){
    this.service = service;
    this.repository = repository;
  }

  @PostMapping("/auth/cadastro")
  @ResponseStatus(HttpStatus.CREATED)
  public UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest dados){
    return service.cadastrar(dados); // Controller delega a regra ao Service
  }

  @PostMapping("/auth/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest dados){
    return service.entrar(dados);
  }

  @GetMapping("/auth/me")
  public UsuarioResponse me(Principal principal){
    Usuario user = repository.findByEmailIgnoreCase(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    return  UsuarioResponse.de(user);
  }

  @GetMapping("/public")
  public Map<String, String> publico() { return Map.of("mensagem", "Rota pública"); }

  @GetMapping("/privado")
  public Map<String, String> privado() { return Map.of("mensagem", "JWT Válido!"); }
}

package com.senai.backend_java.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.senai.backend_java.dto.CadastroRequest;
import com.senai.backend_java.dto.LoginRequest;
import com.senai.backend_java.dto.LoginResponse;
import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;
import com.senai.backend_java.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final AuthService service;
  private final UsuarioRepository repository;

  public AuthController(AuthService service, UsuarioRepository repository){
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

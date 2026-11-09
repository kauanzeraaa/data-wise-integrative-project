package com.senai.backend_java.controllers;

import java.util.Map; // usado para montar resposta simples em JSON

import org.springframework.http.ResponseEntity; // permite definir status HTTP
import org.springframework.web.bind.MethodArgumentNotValidException; // erro de @Valid
import org.springframework.web.bind.annotation.ExceptionHandler; // captura exceções
import org.springframework.web.bind.annotation.RestControllerAdvice; // tratamento global de erro

@RestControllerAdvice
public class ErroHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErroDto(MethodArgumentNotValidException e){
        var erro = e.getBindingResult().getFieldErrors().get(0);

        return ResponseEntity.badRequest().body(Map.of( // devolve HTTP 400
            "mensagem", "Dados inválidos", // mensagem geral
            "campo", erro.getField(), // campo que falhou
            "erro", erro.getDefaultMessage() // texto definido no DTO
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class) // captura erro lançado pelo Service
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(IllegalArgumentException e) { // método do erro
        return ResponseEntity.badRequest().body(Map.of( // devolve HTTP 400
            "mensagem", "Regra de validação não atendida", // mensagem geral
            "erro", e.getMessage() // mensagem específica, exemplo: código repetido
        )); // fim do body
    } // fim do método
}

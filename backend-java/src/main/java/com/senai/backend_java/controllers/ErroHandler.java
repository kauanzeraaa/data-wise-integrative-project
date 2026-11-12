package com.senai.backend_java.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.senai.backend_java.config.ApiException;

/**
 * Tratamento GLOBAL de erros: converte exceções em respostas JSON padronizadas,
 * evitando que o front receba um 500 genérico com stack trace.
 */
@RestControllerAdvice
public class ErroHandler {

    /** Falha do @Valid em um DTO -> HTTP 400 com o primeiro campo inválido. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErroDto(MethodArgumentNotValidException e) {
        Map<String, String> corpo = new LinkedHashMap<>();   // Map.of() não aceita null; LinkedHashMap aceita e mantém a ordem
        corpo.put("mensagem", "Dados inválidos");
        var erro = e.getBindingResult().getFieldError();      // null quando o erro não é de um campo (ex.: validação da classe toda)
        if (erro != null) {
            corpo.put("campo", erro.getField());
            corpo.put("erro", erro.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(corpo);
    }

    /** Regra de negócio violada (lançada nos services) -> HTTP 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(IllegalArgumentException e) {
        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put("mensagem", "Regra de validação não atendida");
        corpo.put("erro", e.getMessage());                   // ex.: "Linha 3: codigo_cliente: código duplicado"
        return ResponseEntity.badRequest().body(corpo);
    }

    /** Erros "nossos" (ApiException) -> usa o status HTTP que o service escolheu (401, 404, 422, 503...). */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, String>> tratarApiException(ApiException e) {
        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put("mensagem", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(corpo);
    }
}

package com.senai.backend_java.config;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {           // erro nosso, com código HTTP e mensagem para o usuário
  private final HttpStatus status;                           // guarda o código HTTP do erro

  public ApiException(HttpStatus status, String mensagem) {  // construtor: recebe o código e a mensagem
      super(mensagem);                                       // passa a mensagem para a classe mãe (RuntimeException)
      this.status = status;                                  // guarda o código recebido
  }                                                          // fecha o bloco

  public HttpStatus getStatus() {                            // getter: quem captura o erro lê o código por aqui
      return status;                                         // devolve o código HTTP
  }                                                          // fecha o bloco
}  
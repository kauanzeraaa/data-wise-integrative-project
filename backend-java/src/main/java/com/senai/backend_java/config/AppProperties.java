package com.senai.backend_java.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties (prefix = "app")                                            // lê tudo que está em "app:" no application.properties
public record AppProperties(Jwt jwt, Cors cors, Admin admin, Upload upload, Etl etl) {  // record = classe só de dados; cada parte é uma seção do yml
    public record Jwt(String secret, long expirationMinutes) {}                     // jwt: segredo que assina o token e quanto tempo ele vale
    public record Cors(List<String> origins) {}                                     // cors: quais endereços de front podem chamar a API
    public record Admin(String nome, String email, String senha) {}                 // admin: dados do administrador criado na 1ª vez
    public record Upload(int maxLinhas) {}                                          // upload: máximo de linhas aceitas por envio
    /** url do serviço Python (API FastAPI) e a chave combinada com ele (vazia = sem chave, só para estudo). */
    public record Etl(String url, String key) {}                                    // etl: endereço da API do Python e a chave
}  
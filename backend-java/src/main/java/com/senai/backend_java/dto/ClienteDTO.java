package com.senai.backend_java.dto;

import java.time.LocalDate; // usado para representar data sem hora

import jakarta.validation.constraints.NotBlank; // valida texto obrigatório
import jakarta.validation.constraints.NotNull; // valida valor obrigatório
import jakarta.validation.constraints.Pattern; // valida formato do texto
import jakarta.validation.constraints.Positive; // valida número maior que zero

/**
 * Cliente já tipado e validado (entrada/saída de POST /api/clientes/validar).
 * As anotações são checadas pelo @Valid do controller ANTES de chegar no service.
 * Para a linha crua da planilha (tudo texto), use LinhaPlanilha.
 */
public record ClienteDTO(

    @NotBlank (message = "O código do cliente é obrigatório")
    String codigoCliente,

    @NotBlank (message = "O nome do cliente é obrigatório")
    String nomeCliente,
    
    @NotBlank(message = "O consultor é obrigatório")
    String consultor,

    @NotBlank(message = "O segmento é obrigatório")
    String segmento,

    @NotBlank(message = "O nível do cliente é obrigatório")
    @Pattern(regexp = "[ABC]", message = "O nível do cliente deve ser A, B ou C")
    String nivelCliente,
    
    @NotNull(message = "O faturamento anual é obrigatório")
    @Positive(message = "O faturamento anual deve ser maior do que zero")
    Double faturamentoAnual,

    @NotBlank(message = "Informe os serviços contratados")
    String servicosContratados,

    @NotNull(message = "A data de contratação é obrigatória")
    LocalDate dataContratacao,

    String cidade,

    @Pattern(regexp = "^$|^[A-Za-z]{2}$", message = "A UF deve possuir apenas duas letras")
    String uf
) {
}

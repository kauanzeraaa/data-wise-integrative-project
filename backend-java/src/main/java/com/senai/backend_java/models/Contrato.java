package com.senai.backend_java.models;

import java.math.BigDecimal;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados
import java.time.LocalDate;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.EnumType; // biblioteca do JPA para mapear a estratégia de geração de valores da chave primária
import jakarta.persistence.Enumerated;             // biblioteca do JPA para mapear a chave primária da entidade
import jakarta.persistence.GeneratedValue;     // biblioteca do JPA para mapear a coluna que representa a chave estrangeira na tabela do banco de dados
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;          // biblioteca do JPA para mapear a tabela do banco de dados
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name = "contrato")
@Check (constraints = "data_fim IS NULL OR data_fim < data_inicio")
@Check (constraints = "valor >= 0")
public class Contrato {
  @Id 
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  @Column(name = "id_contrato")
  private Long idContrato;

  @ManyToOne
  @JoinColumn (name = "id_cliente")
  private Cliente cliente;

  @ManyToOne
  @JoinColumn (name = "id_servico")
  private Servico servico;

  @Column (name = "valor", nullable = false, precision = 15, scale = 2)
  private BigDecimal valor;

  @Column (name = "data_inicio", nullable = false)
  private LocalDate dataInicio;

  @Column (name = "data_fim", nullable = false)
  private LocalDate dataFim;

  @Enumerated(EnumType.STRING)
  @Column (name = "status")
  private StatusContrato status = StatusContrato.ATIVO;

  public Contrato() {
  }

  public Contrato(Long idContrato, Cliente cliente, Servico servico, LocalDate dataInicio, LocalDate dataFim,
    StatusContrato status) {
    this.idContrato = idContrato;
    this.cliente = cliente;
    this.servico = servico;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.status = status;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public Servico getServico() {
    return servico;
  }

  public void setServico(Servico servico) {
    this.servico = servico;
  }

  public LocalDate getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(LocalDate dataInicio) {
    this.dataInicio = dataInicio;
  }

  public LocalDate getDataFim() {
    return dataFim;
  }

  public void setDataFim(LocalDate dataFim) {
    this.dataFim = dataFim;
  }

  public StatusContrato getStatus() {
    return status;
  }

  public void setStatus(StatusContrato status) {
    this.status = status;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  
}

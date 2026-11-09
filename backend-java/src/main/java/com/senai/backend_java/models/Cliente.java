package com.senai.backend_java.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados
import jakarta.persistence.Entity;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados
import jakarta.persistence.Enumerated;     // biblioteca do JPA para mapear o atributo como um enumerado no banco de dados
import jakarta.persistence.EnumType;       // biblioteca do JPA para definir o armazenamento do enumerado
import jakarta.persistence.GeneratedValue; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.GenerationType; // biblioteca do JPA para mapear a estratégia de geração de valores da chave primária
import jakarta.persistence.Id;             // biblioteca do JPA para mapear a chave primária da entidade
import jakarta.persistence.JoinColumn;     // biblioteca do JPA para mapear a coluna que representa a chave estrangeira na tabela do banco de dados
import jakarta.persistence.ManyToOne;      // biblioteca do JPA para mapear o relacionamento muitos para um entre entidades
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;          // biblioteca do JPA para mapear a tabela do banco de dados

@Entity // define a classe como uma entidade do banco de dados
@Table(name = "cliente") // nome da tabela no banco de dados
public class Cliente { // define a classe Cliente como uma entidade do banco de dados e herda da classe Pessoa
  @Id // define o atributo como chave primária
  @GeneratedValue (strategy = GenerationType.IDENTITY) // define a estratégia de geração do valor da chave primária
  @Column (name = "id_cliente") // nome da coluna no banco de dados

  private Long idCliente; // atributo que representa a chave primária da tabela cliente

  @ManyToOne // define o relacionamento muitos para um com a entidade Consultor
  @JoinColumn(name="id_consultor", nullable = false) // define a coluna que representa a chave estrangeira na tabela cliente
  private Consultor consultor; // atributo que representa a chave estrangeira da tabela cliente

  @OneToMany(mappedBy = "cliente")
  private List<Contrato> contratos = new ArrayList<>();

  @Column(name = "nome_empresa", nullable = false) // define a coluna nome_empresa como não nula
  private String nomeEmpresa; // atributo que representa o nome da empresa do cliente

  @Column(name = "segmento", nullable = false) // define a coluna segmento como não nula
  private String segmento; // atributo que representa o segmento da empresa do cliente

  @Column (name = "faturamento_anual", nullable = false, precision = 15, scale = 2) // define a coluna faturamento_anual como não nula
  private BigDecimal faturamentoAnual; // atributo que representa o faturamento anual da empresa do cliente

  @Enumerated(EnumType.STRING) // define que o atributo nivel será armazenado como uma string no banco de dados
  @Column(name = "nivel", nullable = false) // define a coluna nivel como não nula
  private NivelCliente nivel; // atributo que representa o nivel do cliente

  @Enumerated(EnumType.STRING) // define que o atributo status será armazenado como uma string no banco de dados
  @Column(name = "status", nullable = false) // define a coluna status como não nula
  private StatusCliente status = StatusCliente.ATIVO; // atributo que representa o status do cliente

  public Cliente() {
  }

  public Cliente(Long idCliente, Consultor consultor, String nomeEmpresa, String segmento, BigDecimal faturamentoAnual, NivelCliente nivel, StatusCliente status) {
    this.idCliente = idCliente;
    this.consultor = consultor;
    this.nomeEmpresa = nomeEmpresa;
    this.segmento = segmento;
    this.faturamentoAnual = faturamentoAnual;
    this.nivel = nivel;
    this.status = status;
  }

  // Getters e Setters
  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Consultor getConsultor() {
    return consultor;
  }

  public void setConsultor(Consultor consultor) {
    this.consultor = consultor;
  }

  public String getNomeEmpresa() {
    return nomeEmpresa;
  }

  public void setNomeEmpresa(String nomeEmpresa) {
    this.nomeEmpresa = nomeEmpresa;
  }

  public String getSegmento() {
    return segmento;
  }

  public void setSegmento(String segmento) {
    this.segmento = segmento;
  }

  public BigDecimal getFaturamentoAnual() {
    return faturamentoAnual;
  }

  public void setFaturamentoAnual(BigDecimal faturamentoAnual) {
    this.faturamentoAnual = faturamentoAnual;
  }

  public NivelCliente getNivel() {
    return nivel;
  }

  public void setNivel(NivelCliente nivel) {
    this.nivel = nivel;
  }

  public StatusCliente getStatus() {
    return status;
  }

  public void setStatus(StatusCliente status) {
    this.status = status;
  }

  public List<Contrato> getContratos() {
    return contratos;
  }

  public void setContratos(List<Contrato> contratos) {
    this.contratos = contratos;
  }
  
}

package com.senai.backend_java.models;

import java.util.ArrayList;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados
import java.util.List;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados

import jakarta.persistence.Column; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.Entity; // biblioteca do JPA para mapear a estratégia de geração de valores da chave primária
import jakarta.persistence.GeneratedValue;             // biblioteca do JPA para mapear a chave primária da entidade
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;          // biblioteca do JPA para mapear a tabela do banco de dados
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "servico")
public class Servico {
  @Id 
  @GeneratedValue (strategy = GenerationType.IDENTITY)
  @Column(name = "id_servico")
  private Long idServico;

  @Column (name = "nome", length = 150, nullable = false)
  private String nomeServico;

  @Column (name = "descricao", nullable = false)
  private String descricao;

  @Column(name = "categoria", length = 100, nullable = false)
  private String categoria;

  @OneToMany(mappedBy = "servico")
  private List<Contrato> contratos = new ArrayList<>();

  public Servico() {
  }

  public Servico(Long idServico, String nomeServico, String descricao, String categoria, Contrato contrato) {
    this.idServico = idServico;
    this.nomeServico = nomeServico;
    this.descricao = descricao;
    this.categoria = categoria;
    this.contratos.add(contrato);
  }

  public Long getIdServico() {
    return idServico;
  }

  public void setIdServico(Long idServico) {
    this.idServico = idServico;
  }

  public String getNomeServico() {
    return nomeServico;
  }

  public void setNomeServico(String nomeServico) {
    this.nomeServico = nomeServico;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public List<Contrato> getContratos() {
    return contratos;
  }

  public void setContratos(List<Contrato> contratos) {
    this.contratos = contratos;
  }

  
}

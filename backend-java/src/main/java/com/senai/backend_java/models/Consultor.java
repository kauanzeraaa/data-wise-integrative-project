package com.senai.backend_java.models;

import java.util.ArrayList;                // biblioteca do Java para utilizar listas
import java.util.List;                     // biblioteca do Java para utilizar listas

import jakarta.persistence.Column;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados
import jakarta.persistence.Entity;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados
import jakarta.persistence.GeneratedValue; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.GenerationType; // biblioteca do JPA para mapear a estratégia de geração de valores da chave primária
import jakarta.persistence.Id;             // biblioteca do JPA para mapear a chave primária da entidade
import jakarta.persistence.OneToMany;      // biblioteca do JPA para mapear o relacionamento um para muitos entre entidades
import jakarta.persistence.Table;          // biblioteca do JPA para mapear a tabela do banco de dados

@Entity
@Table(name = "consultor")
public class Consultor {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column (name = "id_consultor")
  private Long idConsultor;

  @Column (name = "nome", nullable = false, length = 150)
  private String nome;

  @Column (name = "email", nullable = false, length = 150, unique = true)
  private String email;

  @Column (name = "senha", nullable = false)
  private String senha;

  @Column (name = "telefone", nullable = false, length = 30)
  private String telefone;

  @OneToMany(mappedBy = "consultor")
  private List<Cliente> clientes = new ArrayList<>();

  public Consultor() {
  }

  public Consultor(Long idConsultor, String nome, String email, String telefone, String senha) {
    this.idConsultor = idConsultor;
    this.nome = nome;
    this.email = email;
    this.telefone = telefone;
    this.senha = senha;
  }

  public Long getIdConsultor() {
    return idConsultor;
  }

  public void setIdConsultor(Long idConsultor) {
    this.idConsultor = idConsultor;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public List <Cliente> getClientes() {
    return clientes;
  }

  public void setClientes(List <Cliente> clientes) {
    this.clientes = clientes;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }
}

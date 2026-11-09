package com.senai.backend_java.models;

import java.time.LocalDate;

import jakarta.persistence.Column;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados
import jakarta.persistence.Entity;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; // biblioteca do JPA para mapear a chave primária da entidade
import jakarta.persistence.Table;

@Entity
@Table(name = "insight")
public class Insight{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insight")
    private Long idInsight;

    @ManyToOne
    @JoinColumn (name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne 
    @JoinColumn (name = "id_contrato", nullable = false)
    private  Contrato contrato;

    @Column (name = "tipo", nullable=false, length=100)
    private String tipo;

    @Column (name = "descricao", nullable=false)
    private String descricao;

    @Column (name = "data_geracao", nullable = false)
    private LocalDate dataGeracao;

    public Insight() {
    }

    public Insight(Long idInsight, Cliente cliente, Contrato contrato, String tipo, String descricao,
            LocalDate dataGeracao) {
        this.idInsight = idInsight;
        this.cliente = cliente;
        this.contrato = contrato;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataGeracao = dataGeracao;
    }

    public Long getIdInsight() {
        return idInsight;
    }

    public void setIdInsight(Long idInsight) {
        this.idInsight = idInsight;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    
}
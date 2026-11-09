package com.senai.backend_java.models;

import java.time.LocalDate;         // biblioteca do JPA para mapear a coluna da tabela do banco de dados

import jakarta.persistence.Column;         // biblioteca do JPA para mapear a classe como uma entidade do banco de dados
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType; // biblioteca do JPA para mapear a geração de valores da chave primária
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="telemetria")
public class Telemetria {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telemetria")
    private Long idTelemetria;

    @ManyToOne 
    @JoinColumn (name = "id_consultor", nullable=false)
    private Consultor consultor;

    @Column (name = "arquivo_nome", nullable=false, length=250)
    private String name;

    @Column (name = "data_hora", nullable=false)
    private LocalDate dataHora;

    @Enumerated (EnumType.STRING)
    @Column (name = "evento")
    private TipoEvento evento = TipoEvento.PROCESSAMENTO;

    @Enumerated (EnumType.STRING)
    @Column (name = "status")
    private StatusTelemetria status = StatusTelemetria.SUCESSO;

    @Column (name = "mensagem_erro")
    private String mensagemErro;

    public Telemetria() {
    }

    public Telemetria(Long idTelemetria, Consultor consultor, String name, LocalDate dataHora, TipoEvento evento,
            StatusTelemetria status, String mensagemErro) {
        this.idTelemetria = idTelemetria;
        this.consultor = consultor;
        this.name = name;
        this.dataHora = dataHora;
        this.evento = evento;
        this.status = status;
        this.mensagemErro = mensagemErro;
    }

    public Long getIdTelemetria() {
        return idTelemetria;
    }

    public void setIdTelemetria(Long idTelemetria) {
        this.idTelemetria = idTelemetria;
    }

    public Consultor getConsultor() {
        return consultor;
    }

    public void setConsultor(Consultor consultor) {
        this.consultor = consultor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDate dataHora) {
        this.dataHora = dataHora;
    }

    public TipoEvento getEvento() {
        return evento;
    }

    public void setEvento(TipoEvento evento) {
        this.evento = evento;
    }

    public StatusTelemetria getStatus() {
        return status;
    }

    public void setStatus(StatusTelemetria status) {
        this.status = status;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    
}

package com.senai.backend_java.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity // Relaciona esta classe a uma entidade persistente.
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Identificador gerado pelo banco.
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false)
    private String senhaHash; // Nunca armazenar a senha original.

    @Column(length = 30)                                                           // coluna opcional
    private String matricula; 

    @Column(length = 60)                                                           // coluna opcional
    private String turma;

    @Enumerated(EnumType.STRING) // Guarda USER/ADMIN como texto no PostgreSQL.
    @Column(nullable = false, length = 20)
    private Profile profile = Profile.USER;

    @Enumerated(EnumType.STRING)                                                   // guarda o nome do enum como texto
    @Column(nullable = false, length = 20)                                         // coluna obrigatória
    private StatusUsuario status = StatusUsuario.PENDENTE; 

    @Column(name = "criado_em", nullable = false)                                  // coluna criado_em
    private LocalDateTime criadoEm;

    public Usuario() {} // Construtor exigido pelo JPA.

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public Profile getPerfil() { return profile; }
    public void setPerfil(Profile profile) { this.profile = profile; }

    public boolean isAdmin() { return profile == Profile.ADMIN; }             // atalho: true se o perfil é ADMIN
}

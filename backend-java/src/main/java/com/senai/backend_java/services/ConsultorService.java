package com.senai.backend_java.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.senai.backend_java.models.Consultor;
import com.senai.backend_java.repositories.ConsultorRepository;

import jakarta.transaction.Transactional;

@Service 
public class ConsultorService {

  // Create the Consultor variable
  private final ConsultorRepository repository;

  // Create construtor
  public ConsultorService(ConsultorRepository repository){
    this.repository = repository;
  }

  // Create
  @Transactional
  public Consultor criar(Consultor consultor){
    if(consultor.getNome() == null || consultor.getNome().isBlank()){
      throw new RuntimeException("Nome é obrigatório!");
    }

    if(consultor.getEmail() == null || consultor.getEmail().isBlank()){
      throw new RuntimeException("E-mail é obrigatório!");
    }

    if(consultor.getSenha() == null || consultor.getSenha().isBlank()){
      throw new RuntimeException("Senha é obrigatória!");
    }

    // Verify if consultor is already created
    if(repository.findByEmail(consultor.getEmail()).isPresent()){
      throw new RuntimeException("E-mail já cadastrado!");
    }
    
    return repository.save(consultor);
  }

  // Create login function
  public Consultor login(String email, String senha){
    Consultor consultor = repository.findByEmail(email).orElseThrow(()->new RuntimeException("Consultor não encontrado"));

    // Validation
    if(!consultor.getSenha().equals(senha)){
      throw new RuntimeException("Senha inválida!");
    }

    return consultor;
  }

  // Read - All
  public List<Consultor> listar(){
    return repository.findAll();
  }

  // Read by ID
  public Consultor buscarPorId(Long id){
    return repository.findById(id).orElseThrow(()-> new RuntimeException("Consultor não encontrado!"));
  }

  // Update
  @Transactional 
  public Consultor atualizar(Long id, Consultor dados){
    Consultor consultor = buscarPorId(id);

    consultor.setNome(dados.getNome());
    consultor.setEmail(dados.getEmail());
    consultor.setSenha(dados.getSenha());

    return repository.save(consultor);
  }

  // Delete
  @Transactional
  public Consultor deletar(Long id){
    Consultor consultor = buscarPorId(id);

    repository.deleteById(consultor.getIdConsultor());
    return consultor;
  }
}

package com.senai.backend_java.services;

import org.springframework.stereotype.Service;

import com.senai.backend_java.models.Cliente;
import com.senai.backend_java.models.Consultor;
import com.senai.backend_java.repositories.ClienteRepository;
import com.senai.backend_java.repositories.ConsultorRepository;

import jakarta.transaction.Transactional;

@Service 
public class ClienteService {
  private final ClienteRepository clienteRepository;
  private final ConsultorRepository consultorRepository;

  public ClienteService(ClienteRepository clienteRepository, ConsultorRepository consultorRepository){
    this.clienteRepository = clienteRepository;
    this.consultorRepository = consultorRepository;
  }

  // Create
  @Transactional
  public Cliente criar(Long idConsultor, Cliente cliente){
    // Verify if Consultor already exists
    Consultor consultor = consultorRepository.findById(idConsultor).orElseThrow(()->new RuntimeException("Consultor não encotrado!"));

    // Verify if field NomeEmpresa is empty
    if(cliente.getNomeEmpresa() == null || cliente.getNomeEmpresa().isBlank()){
      throw new RuntimeException("Nome da empresa é obrigatório!");
    }

    // Verify if field Segmento is empty
    if(cliente.getSegmento() == null || cliente.getSegmento().isBlank()){
      throw new RuntimeException("Segmento é obrigatório");
    }

    // Verify if field Faturamento Anual is empty
    if(cliente.getFaturamentoAnual() == null){
      throw new RuntimeException("Faturamento anual é obrigatório!");
    }

    // Verify if field Nivel Anual is empty
    if(cliente.getNivel() == null){
      throw new RuntimeException("Nivel é obrigatório!");
    }

    // Verify if field Status Anual is empty
    if(cliente.getStatus() == null){
      throw new RuntimeException("Status é obrigatório!");
    }

    cliente.setConsultor(consultor);
    return clienteRepository.save(cliente);
  }

  // Read by Id
  public Cliente buscarPorId(Long id){
    return clienteRepository.findById(id).orElseThrow(()->new RuntimeException("Cliente não encontrado"));
  }

  // Update
  public Cliente atualizar(Long id, Cliente dados){
    Cliente cliente = buscarPorId(id);

    cliente.setNomeEmpresa(dados.getNomeEmpresa());
    cliente.setSegmento(dados.getSegmento());
    cliente.setFaturamentoAnual(dados.getFaturamentoAnual());
    cliente.setNivel(dados.getNivel());
    cliente.setStatus(dados.getStatus());

    return clienteRepository.save(cliente);
  }

  // Delete
  @Transactional 
  public void excluir(Long id){
    Cliente cliente = buscarPorId(id);

    clienteRepository.deleteById(cliente.getIdCliente());
  }
}

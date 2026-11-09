package com.senai.backend_java.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.backend_java.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
  
}

package com.senai.backend_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.senai.backend_java.models.Consultor;

public interface ConsultorRepository extends JpaRepository<Consultor, Long>{
  // Verify if Consultor already exists by Email
  Optional<Consultor>findByEmail(String email);



}
package com.senai.backend_java.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.senai.backend_java.dto.UploadDTO.EnvioValidadasRequest;
import com.senai.backend_java.services.UploadService;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
  private final UploadService service;

  public UploadController(UploadService service){
    this.service = service;
  }

  @PostMapping("/validadas")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> enviarValidadas(@RequestBody EnvioValidadasRequest pedido){
    return service.receberValidadas(pedido);
  }
  
}

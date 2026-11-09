package com.senai.backend_java.controllers;

import com.senai.backend_java.dto.ClienteDTO;
import com.senai.backend_java.services.ClienteValidacaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteValidacaoController {
    private final ClienteValidacaoService clienteService;

    public ClienteValidacaoController(ClienteValidacaoService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/validar")
    public ResponseEntity<List<ClienteDTO>> validarClientes(@Valid @RequestBody List<@Valid ClienteDTO> clientes) {
        List<ClienteDTO> preparados = clienteService.validarEPreparar(clientes);
        return ResponseEntity.ok(preparados);
    }
}

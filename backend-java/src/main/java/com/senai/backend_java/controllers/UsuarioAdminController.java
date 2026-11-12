package com.senai.backend_java.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.senai.backend_java.dto.UsuarioResponse;
import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.services.UsuarioService;

/**
 * Tela de liberação de acesso (somente ADMIN).
 * <ul>
 *   <li>GET   /api/admin/usuarios?status=PENDENTE – lista usuários (filtro opcional)</li>
 *   <li>PATCH /api/admin/usuarios/{id}/status     – corpo: {"status": "APROVADO"}</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")   // funciona porque o SecurityConfig tem @EnableMethodSecurity
public class UsuarioAdminController {
    private final UsuarioService service;

    public UsuarioAdminController(UsuarioService service) {
        this.service = service;
    }

    /** Corpo do PATCH. Record dentro do controller porque só é usado aqui. */
    public record AlterarStatusRequest(StatusUsuario status) {}

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) StatusUsuario status) {
        return service.listar(status);
    }

    @PatchMapping("/{id}/status")
    public UsuarioResponse alterarStatus(@PathVariable Long id, @RequestBody AlterarStatusRequest pedido,
                                         Authentication authentication) {
        return service.alterarStatus(id, pedido.status(), service.atual(authentication));
    }
}

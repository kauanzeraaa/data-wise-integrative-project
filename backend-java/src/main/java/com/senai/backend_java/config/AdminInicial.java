package com.senai.backend_java.config;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.senai.backend_java.models.Profile;
import com.senai.backend_java.models.StatusUsuario;
import com.senai.backend_java.models.Usuario;
import com.senai.backend_java.repositories.UsuarioRepository;

/**
 * Cria o primeiro ADMIN (já APROVADO) quando a aplicação sobe, usando app.admin.* do application.properties.
 *
 * <p>Necessário porque todo cadastro novo nasce PENDENTE: sem um admin inicial ninguém conseguiria aprovar ninguém.
 * Se o e-mail já existir, não faz nada (é seguro reiniciar a aplicação).
 */
@Component
public class AdminInicial implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final AppProperties props;
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public AdminInicial(AppProperties props, UsuarioRepository repo, PasswordEncoder encoder) {
        this.props = props;
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Admin admin = props.admin();
        if (admin == null || vazio(admin.email()) || vazio(admin.senha())) {
            log.warn("app.admin não configurado: nenhum administrador inicial foi criado.");
            return;
        }
        String email = admin.email().trim().toLowerCase(Locale.ROOT);
        if (repo.existsByEmailIgnoreCase(email)) return;   // já existe: não sobrescreve senha nem dados

        Usuario u = new Usuario();
        u.setNome(vazio(admin.nome()) ? "Administrador" : admin.nome().trim());
        u.setEmail(email);
        u.setSenhaHash(encoder.encode(admin.senha()));
        u.setPerfil(Profile.ADMIN);
        u.setStatus(StatusUsuario.APROVADO);
        repo.save(u);
        log.info("Administrador inicial criado: {}", email);
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}

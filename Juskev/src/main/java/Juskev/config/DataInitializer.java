package Juskev.config;


import Juskev.model.Usuario;
import Juskev.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void run(String... args) throws Exception {
        // Crear directorio de uploads si no existe
        Files.createDirectories(Paths.get(uploadDir));
        Files.createDirectories(Paths.get("src/main/resources/static/uploads/productos"));

        // Crear usuario administrador si no existe
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                .nombre("Administrador JUSKEV")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .rol(Usuario.Rol.ADMIN)
                .activo(true)
                .build();
            usuarioRepository.save(admin);
            log.info("✅ Usuario administrador creado: {}", adminEmail);
        }
    }
}
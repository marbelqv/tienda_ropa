package Juskev.service;


import Juskev.model.Usuario;
import Juskev.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(String nombre, String email, String password) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
        }
        Usuario usuario = Usuario.builder()
            .nombre(nombre)
            .email(email)
            .password(passwordEncoder.encode(password))
            .rol(Usuario.Rol.CLIENTE)
            .activo(true)
            .build();
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public long totalClientes() {
        return usuarioRepository.findAll().stream()
            .filter(u -> u.getRol() == Usuario.Rol.CLIENTE)
            .count();
    }

    public java.util.List<Usuario> obtenerTodosLosClientes() {
        return usuarioRepository.findAll().stream()
            .filter(u -> u.getRol() == Usuario.Rol.CLIENTE)
            .toList();
    }
}
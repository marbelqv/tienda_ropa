package Juskev.security;


import Juskev.model.Usuario;
import Juskev.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Acceso público al sitio
                .requestMatchers(
                    "/", "/inicio", "/nosotros", "/catalogo", "/contacto","/guia-tallas",
                    "/css/**", "/js/**", "/img/**", "/uploads/**",
                    "/api/productos/**",
                    "/api/producto/**",
                    "/auth/login", "/auth/registro",
                    "/auth/login-error"
                ).permitAll()

                // Solo ADMIN puede acceder al dashboard
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                // Redirección según rol después del login
                .successHandler((request, response, authentication) -> {
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        // Intentar recuperar la URL que el usuario quería visitar
                        // antes de que Spring Security lo mandara al login
                        HttpSessionRequestCache cache = new HttpSessionRequestCache();
                        SavedRequest savedRequest = cache.getRequest(request, response);
                        if (savedRequest != null) {
                            String targetUrl = savedRequest.getRedirectUrl();
                            response.sendRedirect(targetUrl);
                        } else {
                            // Fallback: parámetro ?redirect= o raíz
                            String redirect = request.getParameter("redirect");
                            response.sendRedirect(redirect != null && !redirect.isEmpty()
                                ? redirect : "/");
                        }
                    }
                })
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado: " + email));

            if (!usuario.isActivo()) {
                throw new UsernameNotFoundException("Usuario inactivo");
            }

            String role = "ROLE_" + usuario.getRol().name(); // ROLE_ADMIN o ROLE_CLIENTE
            return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
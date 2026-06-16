package Juskev.controller;


import Juskev.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String redirect,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
        }
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           HttpServletRequest request,
                           RedirectAttributes ra) {
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("errorRegistro", "Las contraseñas no coinciden.");
            return "redirect:/auth/login?tab=registro";
        }
        if (password.length() < 8) {
            ra.addFlashAttribute("errorRegistro", "La contraseña debe tener al menos 8 caracteres.");
            return "redirect:/auth/login?tab=registro";
        }
        try {
            usuarioService.registrar(nombre, email, password);

            // Auto-login después del registro
            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            ra.addFlashAttribute("mensajeBienvenida", "¡Bienvenido, " + nombre + "!");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorRegistro", e.getMessage());
            return "redirect:/auth/login?tab=registro";
        }
    }
}
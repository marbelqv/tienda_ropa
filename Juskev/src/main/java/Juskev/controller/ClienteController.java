package Juskev.controller;


import Juskev.model.Pedido;
import Juskev.model.Usuario;
import Juskev.service.FacturaPdfService;
import Juskev.service.PedidoService;
import Juskev.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final FacturaPdfService facturaPdfService;

    @GetMapping("/mis-pedidos")
    public String misPedidos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        List<Pedido> pedidos = pedidoService.obtenerPorUsuario(usuario);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("usuario", usuario);
        return "pages/mis-pedidos";
    }

    // ─── Pedido desde el checkout (con datos de envío y método de pago) ───
    @PostMapping("/pedido/checkout")
    @ResponseBody
    public Map<String, Object> crearPedidoCheckout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items =
                (List<Map<String, Object>>) payload.get("items");

            String direccionEnvio = payload.getOrDefault("direccionEnvio", "").toString();
            String telefono       = payload.getOrDefault("telefono", "").toString();
            String metodoPago     = payload.getOrDefault("metodoPago", "EFECTIVO").toString();
            String notasExtra     = payload.getOrDefault("notas", "").toString();

            String notas = "Dirección: " + direccionEnvio
                         + " | Tel: " + telefono
                         + " | Pago: " + metodoPago
                         + (notasExtra.isBlank() ? "" : " | Notas: " + notasExtra);

            Pedido pedido = pedidoService.crearPedido(usuario, items, notas);
            return Map.of("ok", true, "pedidoId", pedido.getId());
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getMessage());
        }
    }

    // ─── Pedido legado (sin datos de envío, mantenido por compatibilidad) ───
    @PostMapping("/pedido")
    @ResponseBody
    public Map<String, Object> crearPedido(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<Map<String, Object>> items) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
            Pedido pedido = pedidoService.crearPedido(usuario, items, null);
            return Map.of("ok", true, "pedidoId", pedido.getId());
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getMessage());
        }
    }

    // ─── FACTURA PDF (solo el dueño del pedido puede descargarla) ───
    @GetMapping("/factura/{pedidoId}")
    public ResponseEntity<byte[]> descargarFactura(
            @PathVariable Long pedidoId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());
        Pedido pedido = pedidoService.obtenerPorId(pedidoId);

        // Verificar que el pedido existe y pertenece al usuario logueado
        if (pedido == null || pedido.getUsuario() == null
                || !pedido.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf = facturaPdfService.generarFactura(pedido);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=factura-" + String.format("%05d", pedidoId) + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
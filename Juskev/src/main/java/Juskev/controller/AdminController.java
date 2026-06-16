package Juskev.controller;


import Juskev.model.*;
import Juskev.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final FacturaPdfService facturaPdfService;

    // ─── DASHBOARD PRINCIPAL ───
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("totalVentas", pedidoService.totalVentas());
        model.addAttribute("ventasMes", pedidoService.ventasDelMes());
        model.addAttribute("pedidosPendientes", pedidoService.totalPedidosPendientes());
        model.addAttribute("totalProductos", productoService.totalProductosActivos());
        model.addAttribute("totalClientes", usuarioService.totalClientes());
        model.addAttribute("ultimosPedidos",
            pedidoService.obtenerTodos().stream().limit(5).toList());
        model.addAttribute("stockBajo", productoService.obtenerConStockBajo());
        return "admin/dashboard";
    }

    // ─── PRODUCTOS ───
    @GetMapping("/productos")
    public String listaProductos(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("productos", productoService.obtenerTodosParaAdmin());
        model.addAttribute("categorias", Producto.Categoria.values());
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", Producto.Categoria.values());
        model.addAttribute("accion", "Agregar Producto");
        return "admin/producto-form";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        Producto producto = productoService.obtenerPorId(id);
        if (producto == null) return "redirect:/admin/productos";
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", Producto.Categoria.values());
        model.addAttribute("accion", "Editar Producto");
        return "admin/producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam(required = false) MultipartFile imagen,
            @RequestParam(name = "imagenesNuevas", required = false) List<MultipartFile> imagenesExtra,
            RedirectAttributes ra) {
        try {
            productoService.guardar(producto, imagen, imagenesExtra);
            ra.addFlashAttribute("exito", "Producto guardado exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes ra) {
        productoService.eliminar(id);
        ra.addFlashAttribute("exito", "Producto desactivado.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/stock/{id}")
    public String actualizarStock(@PathVariable Long id,
            @RequestParam int stock, RedirectAttributes ra) {
        productoService.actualizarStock(id, stock);
        ra.addFlashAttribute("exito", "Stock actualizado.");
        return "redirect:/admin/productos";
    }

    // ─── STOCK ───
    @GetMapping("/stock")
    public String stock(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("todos", productoService.obtenerTodosParaAdmin());
        model.addAttribute("stockBajo", productoService.obtenerConStockBajo());
        model.addAttribute("sinStock", productoService.obtenerSinStock());
        return "admin/stock";
    }

    // ─── CLIENTES ───
    @GetMapping("/clientes")
    public String clientes(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("clientes", usuarioService.obtenerTodosLosClientes());
        return "admin/clientes";
    }

    // ─── PEDIDOS ───
    @GetMapping("/pedidos")
    public String pedidos(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("pendientes", pedidoService.obtenerPendientes());
        model.addAttribute("completados", pedidoService.obtenerCompletados());
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/estado/{id}")
    public String actualizarEstadoPedido(@PathVariable Long id,
            @RequestParam String estado, RedirectAttributes ra) {
        pedidoService.actualizarEstado(id, Pedido.EstadoPedido.valueOf(estado));
        ra.addFlashAttribute("exito", "Estado del pedido actualizado.");
        return "redirect:/admin/pedidos";
    }

    // ─── VENTA FÍSICA ───
    @GetMapping("/venta-fisica")
    public String ventaFisicaForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("productos", productoService.obtenerProductosActivos());
        return "admin/venta-fisica";
    }

    @PostMapping("/venta-fisica")
    @ResponseBody
    public Map<String, Object> registrarVentaFisica(@RequestBody Map<String, Object> body) {
        try {
            String nombreCliente = (String) body.get("nombreCliente");
            String nombreVendedor = (String) body.get("nombreVendedor");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            Pedido pedido = pedidoService.registrarVentaFisica(nombreCliente, nombreVendedor, items);
            return Map.of("ok", true, "pedidoId", pedido.getId());
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getMessage());
        }
    }

    // ─── FACTURA PDF ───
    @GetMapping("/factura/{pedidoId}")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Long pedidoId) {
        Pedido pedido = pedidoService.obtenerPorId(pedidoId);
        if (pedido == null) return ResponseEntity.notFound().build();

        byte[] pdf = facturaPdfService.generarFactura(pedido);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=factura-" + pedidoId + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    // ─── VISTA PREVIA DEL SITIO ───
    @GetMapping("/vista-previa")
    public String vistaPrevia() {
        return "redirect:/";
    }
}
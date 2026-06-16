package Juskev.controller;


import Juskev.model.Producto;
import Juskev.model.Reseña;
import Juskev.service.ProductoService;
import Juskev.service.ResenaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PublicoController {

    private final ProductoService productoService;
    private final ResenaService resenaService;

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        List<Producto> carrusel = productoService.obtenerParaCarrusel();
        List<Producto> ofertas = productoService.obtenerEnOferta();
        model.addAttribute("carrusel", carrusel);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("paginaActual", "inicio");
        return "pages/inicio";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        model.addAttribute("paginaActual", "nosotros");
        return "pages/nosotros";
    }

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String talla,
            Model model) {

        List<Producto> productos;
        boolean hayFiltros = (nombre != null && !nombre.isBlank())
            || (categoria != null && !categoria.isBlank())
            || precioMin != null || precioMax != null
            || (talla != null && !talla.isBlank());

        if (hayFiltros) {
            productos = productoService.buscarConFiltros(nombre, categoria, precioMin, precioMax, talla);
        } else {
            productos = productoService.obtenerProductosActivos();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", Producto.Categoria.values());
        model.addAttribute("paginaActual", "catalogo");
        model.addAttribute("filtroNombre", nombre);
        model.addAttribute("filtroCategoria", categoria);
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);
        model.addAttribute("filtroTalla", talla);
        return "pages/catalogo";
    }


    
    @GetMapping("/contacto")
    public String contacto(Model model) {
        model.addAttribute("paginaActual", "contacto");
        return "pages/contacto";
    }


    @GetMapping("/checkout")
    public String checkout() {
        return "pages/checkout";
    }

    @GetMapping("/guia-tallas")
    public String guiaTallas() {
        return "pages/guia-tallas";
    }

    /** API JSON — datos completos de un producto para el modal */
    @GetMapping("/api/producto/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiProducto(@PathVariable Long id) {
        Producto p = productoService.obtenerPorId(id);
        if (p == null) return ResponseEntity.notFound().build();

        Map<String, Object> data = new HashMap<>();
        data.put("id",          p.getId());
        data.put("nombre",      p.getNombre());
        data.put("marca",       p.getMarca());
        data.put("descripcion", p.getDescripcion());
        data.put("precio",      p.getPrecioFinal());
        data.put("precioBase",  p.getPrecio());
        data.put("descuento",   p.getDescuento());
        data.put("badge",       p.getBadge());
        data.put("imagenUrl",   p.getImagenUrl());
        data.put("imagenes",    p.getTodasLasImagenes());   // principal + extras
        data.put("tallas",      p.getTallasLista());
        data.put("colores",     p.getColoresLista());
        data.put("stock",       p.getStock());
        return ResponseEntity.ok(data);
    }













    


    @GetMapping("/api/producto/{id}/resenas")
@ResponseBody
public ResponseEntity<?> getResenas(@PathVariable Long id) {
    List<Reseña> resenas = resenaService.obtenerPorProducto(id);
    List<Map<String, Object>> result = resenas.stream().map(r -> {
        Map<String, Object> m = new java.util.HashMap<>();
        m.put("id", r.getId());
        m.put("nombreAutor", r.getNombreAutor());
        m.put("estrellas", r.getEstrellas());
        m.put("comentario", r.getComentario());
        m.put("fechaResena", r.getFechaReseña());
        return m;
    }).toList();
    return ResponseEntity.ok(result);
}

@PostMapping("/api/producto/{id}/resenas")
@ResponseBody
public ResponseEntity<?> guardarResena(@PathVariable Long id,
        @RequestBody Map<String, Object> body,
        org.springframework.security.core.Authentication auth) {
    String nombre = auth != null ? auth.getName() : (String) body.get("nombreAutor");
    Integer estrellas = (Integer) body.get("estrellas");
    String comentario = (String) body.get("comentario");
    if (estrellas == null || estrellas < 1 || estrellas > 5)
        return ResponseEntity.badRequest().body("Estrellas inválidas");
    Reseña r = resenaService.guardar(id, nombre, estrellas, comentario);
    return ResponseEntity.ok(Map.of("ok", true, "id", r.getId()));
}
}
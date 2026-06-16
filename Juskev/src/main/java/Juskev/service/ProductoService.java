package Juskev.service;

import Juskev.model.Producto;
import Juskev.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivoTrueAndStockGreaterThan(0);
    }

    public List<Producto> obtenerTodosParaAdmin() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public List<Producto> obtenerParaCarrusel() {
        return productoRepository.findByEnCarruselTrueAndActivoTrue();
    }

    public List<Producto> obtenerEnOferta() {
        return productoRepository.findByEnOfertaTrueAndActivoTrueAndStockGreaterThan(0);
    }

    // FIX: ahora pasamos categoriaStr directamente como String (native query)
    public List<Producto> buscarConFiltros(String nombre, String categoriaStr,
            BigDecimal precioMin, BigDecimal precioMax, String talla) {

        String nombreParam   = (nombre != null && !nombre.isBlank()) ? nombre : null;
        String categoriaParam = (categoriaStr != null && !categoriaStr.isBlank()) ? categoriaStr : null;
        String tallaParam    = (talla != null && !talla.isBlank()) ? talla : null;

        return productoRepository.buscarConFiltros(
            nombreParam, categoriaParam, precioMin, precioMax, tallaParam);
    }

    public Producto guardar(Producto producto, MultipartFile imagen,
                             List<MultipartFile> imagenesNuevas) throws IOException {

        // ── Imagen principal ──
        // Si viene archivo nuevo, guardarlo; si no, conservar la URL que ya venía del form
        if (imagen != null && !imagen.isEmpty()) {
            producto.setImagenUrl(guardarArchivo(imagen));
        } else if (producto.getId() != null && (producto.getImagenUrl() == null || producto.getImagenUrl().isBlank())) {
            // Edición sin nueva imagen y sin hidden → recuperar de BD para no perderla
            Producto existing = productoRepository.findById(producto.getId()).orElse(null);
            if (existing != null) producto.setImagenUrl(existing.getImagenUrl());
        }

        // ── Imágenes extra ──
        if (imagenesNuevas != null) {
            List<String> urls = new java.util.ArrayList<>();
            // Conservar las que ya existían (enviadas como campo oculto)
            if (producto.getImagenesExtra() != null && !producto.getImagenesExtra().isBlank()) {
                java.util.Arrays.stream(producto.getImagenesExtra().split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).forEach(urls::add);
            }
            for (MultipartFile f : imagenesNuevas) {
                if (f != null && !f.isEmpty()) urls.add(guardarArchivo(f));
            }
            if (!urls.isEmpty()) producto.setImagenesExtra(String.join(",", urls));
        }

        if (producto.getDescuento() != null && producto.getDescuento() > 0) {
            producto.setPrecioOriginal(producto.getPrecio());
            producto.setEnOferta(true);
            if (producto.getBadge() == null || producto.getBadge().isBlank()) producto.setBadge("Sale");
        }

        return productoRepository.save(producto);
    }

    /** Guarda un archivo en disco y retorna su URL pública */
    private String guardarArchivo(MultipartFile file) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destino = Paths.get(uploadDir).resolve(nombreArchivo);
        Files.createDirectories(destino.getParent());
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/productos/" + nombreArchivo;
    }

    // Mantener compatibilidad con llamadas que solo pasan imagen principal
    public Producto guardar(Producto producto, MultipartFile imagen) throws IOException {
        return guardar(producto, imagen, null);
    }

    public void eliminar(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            productoRepository.save(p);
        });
    }

    public void actualizarStock(Long id, int nuevoStock) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setStock(nuevoStock);
            productoRepository.save(p);
        });
    }

    public List<Producto> obtenerConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    public List<Producto> obtenerSinStock() {
        return productoRepository.findProductosSinStock();
    }

    public long totalProductosActivos() {
        return productoRepository.countByActivoTrueAndStockGreaterThan(0);
    }
}
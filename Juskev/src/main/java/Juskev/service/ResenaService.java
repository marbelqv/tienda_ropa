package Juskev.service;

import Juskev.model.Reseña;
import Juskev.model.Producto;
import Juskev.repository.ReseñaRepository;
import Juskev.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ReseñaRepository reseñaRepository;
    private final ProductoRepository productoRepository;

    public List<Reseña> obtenerPorProducto(Long productoId) {
        Producto p = productoRepository.findById(productoId).orElseThrow();
        return reseñaRepository.findByProductoOrderByFechaReseñaDesc(p);
    }

    public Double promedioEstrellas(Long productoId) {
        Producto p = productoRepository.findById(productoId).orElseThrow();
        return reseñaRepository.promedioEstrellas(p);
    }

    public Reseña guardar(Long productoId, String nombreAutor,
                          Integer estrellas, String comentario) {
        Producto p = productoRepository.findById(productoId).orElseThrow();
        Reseña r = new Reseña();
        r.setProducto(p);
        r.setNombreAutor(nombreAutor);
        r.setEstrellas(estrellas);
        r.setComentario(comentario);
        r.setFechaReseña(LocalDateTime.now());
        return reseñaRepository.save(r);
    }
}
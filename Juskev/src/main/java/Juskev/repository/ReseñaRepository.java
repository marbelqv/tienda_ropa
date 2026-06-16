package Juskev.repository;


import Juskev.model.Reseña;
import Juskev.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ReseñaRepository extends JpaRepository<Reseña, Long> {
    List<Reseña> findByProductoOrderByFechaReseñaDesc(Producto producto);

    @Query("SELECT AVG(r.estrellas) FROM Reseña r WHERE r.producto = :producto")
    Double promedioEstrellas(Producto producto);
}
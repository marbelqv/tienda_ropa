package Juskev.repository;

import Juskev.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrueAndStockGreaterThan(int stock);

    List<Producto> findByCategoriaAndActivoTrueAndStockGreaterThan(
        Producto.Categoria categoria, int stock);

    List<Producto> findByEnCarruselTrueAndActivoTrue();

    List<Producto> findByEnOfertaTrueAndActivoTrueAndStockGreaterThan(int stock);

    @Query(value = """
        SELECT * FROM productos p
        WHERE p.activo = true
          AND p.stock > 0
          AND (CAST(:nombre AS text) IS NULL OR LOWER(p.nombre) LIKE LOWER('%' || CAST(:nombre AS text) || '%'))
          AND (CAST(:categoria AS text) IS NULL OR p.categoria = CAST(:categoria AS text))
          AND (CAST(:precioMin AS numeric) IS NULL OR p.precio >= CAST(:precioMin AS numeric))
          AND (CAST(:precioMax AS numeric) IS NULL OR p.precio <= CAST(:precioMax AS numeric))
          AND (CAST(:talla AS text) IS NULL OR p.tallas LIKE '%' || CAST(:talla AS text) || '%')
        ORDER BY p.fecha_creacion DESC
    """, nativeQuery = true)
    List<Producto> buscarConFiltros(
        @Param("nombre") String nombre,
        @Param("categoria") String categoria,
        @Param("precioMin") BigDecimal precioMin,
        @Param("precioMax") BigDecimal precioMax,
        @Param("talla") String talla
    );

    long countByActivoTrueAndStockGreaterThan(int stock);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= 5 AND p.stock > 0")
    List<Producto> findProductosConStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock = 0")
    List<Producto> findProductosSinStock();
}
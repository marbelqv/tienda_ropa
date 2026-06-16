package Juskev.repository;


import Juskev.model.DetallePedido;
import Juskev.model.Reseña;
import Juskev.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("""
        SELECT d.producto.nombre, SUM(d.cantidad) as totalVendido
        FROM DetallePedido d
        GROUP BY d.producto.nombre
        ORDER BY totalVendido DESC
    """)
    List<Object[]> productosMasVendidos();
}
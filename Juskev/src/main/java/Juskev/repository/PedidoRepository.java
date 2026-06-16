package Juskev.repository;

import Juskev.model.Pedido;
import Juskev.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);

    List<Pedido> findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido estado);

    List<Pedido> findAllByOrderByFechaPedidoDesc();

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado = 'COMPLETADO'")
    BigDecimal totalVentasCompletadas();

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.estado = 'COMPLETADO' AND p.fechaPedido >= :desde")
    BigDecimal totalVentasDesde(LocalDateTime desde);

    long countByEstado(Pedido.EstadoPedido estado);
}
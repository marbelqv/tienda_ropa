package Juskev.service;

import Juskev.model.*;
import Juskev.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Pedido crearPedido(Usuario usuario, List<Map<String, Object>> items, String notas) {
        BigDecimal total = BigDecimal.ZERO;

        Pedido pedido = Pedido.builder()
            .usuario(usuario)
            .nombreCliente(usuario.getNombre())
            .estado(Pedido.EstadoPedido.PENDIENTE)
            .tipoVenta(Pedido.TipoVenta.ONLINE)
            .fechaPedido(LocalDateTime.now())
            .notas(notas)
            .total(BigDecimal.ZERO)
            .build();
        pedido = pedidoRepository.save(pedido);

        for (Map<String, Object> item : items) {
            Long productoId = Long.valueOf(item.get("productoId").toString());
            int cantidad    = Integer.parseInt(item.get("cantidad").toString());
            String talla    = item.get("talla")  != null ? item.get("talla").toString()  : null;
            String color    = item.get("color")  != null ? item.get("color").toString()  : null;

            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            if (producto.getStock() < cantidad) {
                throw new IllegalStateException("Stock insuficiente para: " + producto.getNombre());
            }

            BigDecimal precioUnitario = producto.getPrecioFinal();
            DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .talla(talla)
                .color(color)
                .build();
            detallePedidoRepository.save(detalle);

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    /**
     * Registrar venta física desde el dashboard admin.
     * Ahora también guarda talla y color por ítem.
     */
    @Transactional
    public Pedido registrarVentaFisica(String nombreCliente, String nombreVendedor,
            List<Map<String, Object>> items) {

        BigDecimal total = BigDecimal.ZERO;

        Pedido pedido = Pedido.builder()
            .nombreCliente(nombreCliente)
            .nombreVendedor(nombreVendedor)
            .estado(Pedido.EstadoPedido.COMPLETADO)
            .tipoVenta(Pedido.TipoVenta.FISICA)
            .fechaPedido(LocalDateTime.now())
            .total(BigDecimal.ZERO)
            .build();
        pedido = pedidoRepository.save(pedido);

        for (Map<String, Object> item : items) {
            Long productoId = Long.valueOf(item.get("productoId").toString());
            int cantidad    = Integer.parseInt(item.get("cantidad").toString());
            String talla    = item.get("talla")  != null ? item.get("talla").toString()  : null;
            String color    = item.get("color")  != null ? item.get("color").toString()  : null;

            Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            BigDecimal precioUnitario = producto.getPrecioFinal();
            DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(precioUnitario)
                .talla(talla)
                .color(color)
                .build();
            detallePedidoRepository.save(detalle);

            producto.setStock(Math.max(0, producto.getStock() - cantidad));
            productoRepository.save(producto);

            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
    }

    public List<Pedido> obtenerPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido.PENDIENTE);
    }

    public List<Pedido> obtenerCompletados() {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido.COMPLETADO);
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void actualizarEstado(Long pedidoId, Pedido.EstadoPedido nuevoEstado) {
        pedidoRepository.findById(pedidoId).ifPresent(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepository.save(p);
        });
    }

    public BigDecimal totalVentas() {
        return pedidoRepository.totalVentasCompletadas();
    }

    public BigDecimal ventasDelMes() {
        return pedidoRepository.totalVentasDesde(LocalDateTime.now().withDayOfMonth(1));
    }

    public long totalPedidosPendientes() {
        return pedidoRepository.countByEstado(Pedido.EstadoPedido.PENDIENTE);
    }
}
package Juskev.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Para ventas físicas el usuario puede ser nulo, se guarda solo el nombre
    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta")
    private TipoVenta tipoVenta = TipoVenta.ONLINE;

    @Column(name = "nombre_vendedor")
    private String nombreVendedor; // para ventas físicas

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    public enum EstadoPedido {
        PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO
    }

    public enum TipoVenta {
        ONLINE, FISICA
    }
}

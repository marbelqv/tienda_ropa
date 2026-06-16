package Juskev.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reseña {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "nombre_autor")
    private String nombreAutor;

    @Column(nullable = false)
    private Integer estrellas; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_resena")
    private LocalDateTime fechaReseña = LocalDateTime.now();
}
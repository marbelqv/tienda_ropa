package Juskev.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "precio_original", precision = 12, scale = 2)
    private BigDecimal precioOriginal; // precio antes del descuento

    // Descuento en porcentaje (0-100). Si es 0, no hay descuento.
    @Column(nullable = false)
    private Integer descuento = 0;

    @Column(nullable = false)
    private Integer stock = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    // Tallas disponibles separadas por coma: "S,M,L,XL"
    private String tallas;

    // Colores en hex separados por coma: "#1a1a1a,#c9a84c"
    private String colores;

    @Column(name = "imagen_url")
    private String imagenUrl;

    // Imágenes adicionales del producto
    @Column(name = "imagenes_extra", columnDefinition = "TEXT")
    private String imagenesExtra;

    private String badge; // "Nuevo", "Sale", null

    @Column(name = "en_oferta")
    private boolean enOferta = false;

    // Rating calculado (0.0 - 5.0)
    private Double rating = 0.0;

    @Column(name = "num_resenas")
    private Integer numResenas = 0;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private boolean activo = true;

    // Indica si aparece en el carrusel del inicio
    @Column(name = "en_carrusel")
    private boolean enCarrusel = false;

    @Column(name = "carrusel_tag")
    private String carruselTag; // "Nueva Colección 2025", etc.

    @Column(name = "carrusel_titulo")
    private String carruselTitulo;

    @Column(name = "carrusel_descripcion")
    private String carruselDescripcion;


@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@com.fasterxml.jackson.annotation.JsonIgnore
private List<Reseña> resenas;


    public enum Categoria {
        ropa, accesorios, calzado, pantalones
    }

    // Precio final (con descuento aplicado)
    public BigDecimal getPrecioFinal() {
        if (descuento != null && descuento > 0) {
            BigDecimal factor = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(descuento).divide(BigDecimal.valueOf(100))
            );
            return precio.multiply(factor).setScale(0, java.math.RoundingMode.HALF_UP);
        }
        return precio;
    }

    public boolean isSinStock() {
        return stock != null && stock <= 0;
    }

    /** Retorna lista de tallas disponibles (split por coma) */
    public java.util.List<String> getTallasLista() {
        if (tallas == null || tallas.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(tallas.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .toList();
    }

    /** Retorna lista de colores en hex (split por coma) */
    public java.util.List<String> getColoresLista() {
        if (colores == null || colores.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(colores.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .toList();
    }

    /** Retorna lista de URLs de imágenes extra (split por coma) */
    public java.util.List<String> getImagenesExtraLista() {
        if (imagenesExtra == null || imagenesExtra.isBlank()) return java.util.List.of();
        return java.util.Arrays.stream(imagenesExtra.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .toList();
    }

    /** Todas las imágenes: principal + extras */
    public java.util.List<String> getTodasLasImagenes() {
        java.util.List<String> lista = new java.util.ArrayList<>();
        if (imagenUrl != null && !imagenUrl.isBlank()) lista.add(imagenUrl);
        lista.addAll(getImagenesExtraLista());
        return lista;
    }
}
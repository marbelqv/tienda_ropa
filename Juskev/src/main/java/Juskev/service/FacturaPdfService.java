package Juskev.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import Juskev.model.DetallePedido;
import Juskev.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class FacturaPdfService {

    private static final DeviceRgb GOLD       = new DeviceRgb(201, 168, 76);
    private static final DeviceRgb DARK       = new DeviceRgb(26, 26, 26);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb MID_GRAY   = new DeviceRgb(120, 120, 120);

    public byte[] generarFactura(Pedido pedido) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter   writer = new PdfWriter(baos);
            PdfDocument pdf    = new PdfDocument(writer);
            Document    doc    = new Document(pdf);
            doc.setMargins(36, 50, 36, 50);

            // ─── ENCABEZADO ───
            Table header = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100));

            Cell logoCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            logoCell.add(new Paragraph("JUSKEV").setFontSize(28).setBold().setFontColor(GOLD));
            logoCell.add(new Paragraph("Moda Masculina Premium").setFontSize(10).setFontColor(MID_GRAY));
            header.addCell(logoCell);

            Cell infoCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
            infoCell.add(new Paragraph("FACTURA DE VENTA").setFontSize(14).setBold().setFontColor(DARK));
            infoCell.add(new Paragraph("N° " + String.format("%05d", pedido.getId()))
                .setFontSize(11).setFontColor(GOLD));
            String fecha = pedido.getFechaPedido()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            infoCell.add(new Paragraph("Fecha: " + fecha).setFontSize(9));
            header.addCell(infoCell);
            doc.add(header);

            // Línea dorada
            doc.add(new Table(UnitValue.createPercentArray(new float[]{100}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(GOLD).setHeight(2)
                .setMarginTop(8).setMarginBottom(16));

            // ─── DATOS CLIENTE / VENDEDOR ───
            Table datos = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

            Cell clienteCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBackgroundColor(LIGHT_GRAY).setPadding(12);
            clienteCell.add(new Paragraph("CLIENTE").setFontSize(9).setBold()
                .setFontColor(MID_GRAY).setMarginBottom(4));
            clienteCell.add(new Paragraph(
                pedido.getNombreCliente() != null ? pedido.getNombreCliente() : "Cliente")
                .setFontSize(12).setBold());
            datos.addCell(clienteCell);

            Cell vendedorCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBackgroundColor(LIGHT_GRAY).setPadding(12);
            vendedorCell.add(new Paragraph("VENDEDOR").setFontSize(9).setBold()
                .setFontColor(MID_GRAY).setMarginBottom(4));
            vendedorCell.add(new Paragraph(
                pedido.getNombreVendedor() != null ? pedido.getNombreVendedor() : "JUSKEV Store")
                .setFontSize(12).setBold());
            datos.addCell(vendedorCell);
            doc.add(datos);

            // ─── TABLA DE PRODUCTOS ───
            // Columnas: Código | Producto | Talla | Color | Cant. | Total
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{10, 34, 12, 14, 12, 18}))
                .setWidth(UnitValue.createPercentValue(100));

            String[] encabezados = {"Código", "Producto", "Talla", "Color", "Cant.", "Total"};
            for (String h : encabezados) {
                tabla.addHeaderCell(new Cell()
                    .setBackgroundColor(DARK)
                    .add(new Paragraph(h).setFontColor(ColorConstants.WHITE).setFontSize(9).setBold())
                    .setPadding(8));
            }

            boolean altRow = false;
            for (DetallePedido detalle : pedido.getDetalles()) {
                DeviceRgb bg = altRow ? LIGHT_GRAY : new DeviceRgb(255, 255, 255);

                // Código
                tabla.addCell(new Cell().setBackgroundColor(bg).setPadding(8)
                    .add(new Paragraph("P-" + String.format("%04d", detalle.getProducto().getId()))
                        .setFontSize(9)));

                // Nombre del producto
                tabla.addCell(new Cell().setBackgroundColor(bg).setPadding(8)
                    .add(new Paragraph(detalle.getProducto().getNombre()).setFontSize(9)));

                // Talla
                String tallaTexto = (detalle.getTalla() != null && !detalle.getTalla().isBlank())
                    ? detalle.getTalla() : "—";
                tabla.addCell(new Cell().setBackgroundColor(bg).setPadding(8)
                    .add(new Paragraph(tallaTexto).setFontSize(9)));

                // Color — se muestra el código hex o "—"
                String colorTexto = (detalle.getColor() != null && !detalle.getColor().isBlank())
                    ? detalle.getColor() : "—";
                Cell colorCell = new Cell().setBackgroundColor(bg).setPadding(8);
                if (!colorTexto.equals("—")) {
                    // Si es un hex válido, pintamos el fondo de la celda con ese color
                    try {
                        int r = Integer.parseInt(colorTexto.substring(1, 3), 16);
                        int g = Integer.parseInt(colorTexto.substring(3, 5), 16);
                        int b = Integer.parseInt(colorTexto.substring(5, 7), 16);
                        DeviceRgb colorRgb = new DeviceRgb(r, g, b);
                        // Celda con pastilla de color + el código
                        colorCell.add(new Paragraph("  " + colorTexto)
                            .setFontSize(8)
                            .setBackgroundColor(colorRgb)
                            .setFontColor(isColorDark(r, g, b)
                                ? ColorConstants.WHITE : ColorConstants.BLACK)
                            .setPadding(2));
                    } catch (Exception ex) {
                        colorCell.add(new Paragraph(colorTexto).setFontSize(9));
                    }
                } else {
                    colorCell.add(new Paragraph("—").setFontSize(9));
                }
                tabla.addCell(colorCell);

                // Cantidad
                tabla.addCell(new Cell().setBackgroundColor(bg).setPadding(8)
                    .add(new Paragraph(String.valueOf(detalle.getCantidad())).setFontSize(9)));

                // Total
                tabla.addCell(new Cell().setBackgroundColor(bg).setPadding(8)
                    .add(new Paragraph("$" + String.format("%,.0f", detalle.getSubtotal()))
                        .setFontSize(9).setBold()));

                altRow = !altRow;
            }
            doc.add(tabla);

            // ─── TOTAL ───
            Table totales = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginTop(8);
            totales.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            Cell totalCell = new Cell()
                .setBackgroundColor(GOLD).setPadding(12)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            totalCell.add(new Paragraph("TOTAL").setFontSize(10).setBold().setFontColor(DARK));
            totalCell.add(new Paragraph("$" + String.format("%,.0f", pedido.getTotal()))
                .setFontSize(18).setBold().setFontColor(DARK));
            totales.addCell(totalCell);
            doc.add(totales);

            // ─── PIE DE PÁGINA ───
            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph("¡Gracias por tu compra en JUSKEV!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(11).setFontColor(GOLD).setBold());
            doc.add(new Paragraph("Moda masculina de alta calidad · Colombia")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(9).setFontColor(MID_GRAY));

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando factura PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    /** Decide si el texto sobre ese color debe ser blanco o negro (contraste). */
    private boolean isColorDark(int r, int g, int b) {
        // Luminancia relativa (fórmula W3C)
        double luminance = 0.299 * r + 0.587 * g + 0.114 * b;
        return luminance < 128;
    }
}
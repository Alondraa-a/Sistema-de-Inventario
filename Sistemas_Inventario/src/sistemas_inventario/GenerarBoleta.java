
package sistemas_inventario;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.awt.Desktop;

public class GenerarBoleta {

    public static void crearPDF(Venta venta) {
        try {
            // Crear carpeta "boletas" si no existe
            File carpeta = new File("boletas");
            if (!carpeta.exists()) carpeta.mkdirs();

            // Archivo dentro de la carpeta
            File archivo = new File(carpeta, "Boleta_" + venta.getCodigo() + ".pdf");

            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            // Contenido del PDF (título, tabla de productos, total, etc.)
            Paragraph titulo = new Paragraph("Boleta de Venta\n\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            documento.add(new Paragraph("Código de venta: " + venta.getCodigo()));
            documento.add(new Paragraph("Cliente: " + venta.getCliente()));
            documento.add(new Paragraph("\nProductos vendidos:\n\n"));

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.addCell("Código");
            tabla.addCell("Nombre");
            tabla.addCell("Cantidad");
            tabla.addCell("Precio");

            for (Producto p : venta.getProductos()) {
                tabla.addCell(p.getCodigo());
                tabla.addCell(p.getNombre());
                tabla.addCell(String.valueOf(p.getCantidad()));
                tabla.addCell("S/ " + p.getPrecio());
            }

            documento.add(tabla);
            documento.add(new Paragraph("\nTotal: S/ " + venta.getTotal(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.RED)));

            documento.close();

            // Abrir automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

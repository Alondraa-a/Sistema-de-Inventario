
package sistemas_inventario;


import java.io.*;
import java.util.ArrayList;

public class GestionArchivo {
    private static final String ARCHIVO = "productos.txt";

    public static void guardarProductos(ArrayList<Producto> productos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Producto p : productos) {
                pw.println(p.getCodigo() + ";" + p.getNombre() + ";" + p.getCantidad() + ";" + p.getPrecio());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Producto> cargarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        File f = new File(ARCHIVO);
        if (!f.exists()) return productos;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                productos.add(new Producto(datos[0], datos[1], Integer.parseInt(datos[2]), Double.parseDouble(datos[3])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productos;
    }

    
}


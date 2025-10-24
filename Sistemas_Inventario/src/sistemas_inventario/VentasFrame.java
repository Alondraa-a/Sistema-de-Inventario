
package sistemas_inventario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VentasFrame extends JFrame {
    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;
    private InventarioFrame inventario;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public VentasFrame(ArrayList<Producto> productos, ArrayList<Venta> ventas, InventarioFrame inventario) {
        this.productos = productos;
        this.ventas = ventas;
        this.inventario = inventario;

        setTitle("Registrar Venta");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Tabla de productos
        String[] columnas = {"Código", "Nombre", "Cantidad", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        actualizarTabla();

        JButton btnVender = new JButton("Registrar Venta");
        btnVender.setBackground(new Color(52, 152, 219));
        btnVender.setForeground(Color.WHITE);
        btnVender.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnVender);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnVender.addActionListener(e -> registrarVenta());
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getCantidad(), p.getPrecio()});
        }
    }

    private void registrarVenta() {
        // Panel principal con scroll
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(productos.size(), 1, 5, 5));

        // Para cada producto, agregamos un panel con checkbox y campo de cantidad
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        ArrayList<JTextField> cantidadFields = new ArrayList<>();

        for (Producto p : productos) {
            JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox check = new JCheckBox(p.getNombre() + " (Stock: " + p.getCantidad() + ")");
            JTextField cantidad = new JTextField(3);
            cantidad.setEnabled(false); // habilitar solo si se selecciona el checkbox
            check.addActionListener(e -> cantidad.setEnabled(check.isSelected()));

            subPanel.add(check);
            subPanel.add(new JLabel(" Cantidad: "));
            subPanel.add(cantidad);

            panel.add(subPanel);
            checkBoxes.add(check);
            cantidadFields.add(cantidad);
        }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(panel),
                "Seleccione productos y cantidades", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        ArrayList<Producto> listaVenta = new ArrayList<>();

        for (int i = 0; i < productos.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                Producto p = productos.get(i);
                String cantStr = cantidadFields.get(i).getText();
                if (cantStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar cantidad para " + p.getNombre());
                    return;
                }
                try {
                    int cant = Integer.parseInt(cantStr);
                    if (cant <= 0 || cant > p.getCantidad()) {
                        JOptionPane.showMessageDialog(this, "Cantidad inválida para " + p.getNombre());
                        return;
                    }
                    listaVenta.add(new Producto(p.getCodigo(), p.getNombre(), cant, p.getPrecio()));
                    p.setCantidad(p.getCantidad() - cant); // actualizar stock
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Número inválido para " + p.getNombre());
                    return;
                }
            }
        }

        if (listaVenta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se seleccionaron productos.");
            return;
        }

        String cliente = JOptionPane.showInputDialog(this, "Nombre del cliente:");
        if (cliente == null || cliente.isEmpty()) cliente = "Cliente";

        // Crear venta
        Venta venta = new Venta("V" + (ventas.size() + 1), cliente, listaVenta);
        ventas.add(venta);

        // Guardar inventario y actualizar tabla
        GestionArchivo.guardarProductos(productos);
        inventario.actualizarTabla();

        // Generar boleta automáticamente
        GenerarBoleta.crearPDF(venta);

        JOptionPane.showMessageDialog(this, "Venta registrada y boleta generada.");
        actualizarTabla();
    }
}
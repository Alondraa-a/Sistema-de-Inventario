/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemas_inventario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;

public class InventarioFrame extends JFrame {
    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;
    private DashboardFrameGrafico dashboard;

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField txtBuscar;

    public InventarioFrame() {
        setTitle("Gestión Librería - Inventario");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setLayout(new BorderLayout(15, 15));
        
        Color colorFondo = new Color(240, 240, 240);
        Color colorBoton = new Color(75, 175, 255);
        Color colorTextoBoton = Color.WHITE;
        
        getContentPane().setBackground(colorFondo);

        productos = GestionArchivo.cargarProductos();
        ventas = new ArrayList<>();

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBusqueda.setBorder(new TitledBorder("Buscar Producto"));
        txtBuscar = new JTextField(25);
        panelBusqueda.add(new JLabel("Nombre o Código:"));
        panelBusqueda.add(txtBuscar);

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { actualizarFiltro(); }
            public void removeUpdate(DocumentEvent e) { actualizarFiltro(); }
            public void insertUpdate(DocumentEvent e) { actualizarFiltro(); }
        });
        
        add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"Código", "Nombre", "Cantidad", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.setRowHeight(25);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actualizarTabla();

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Uso la ruta de clase para mayor robustez
        JButton btnAgregar = crearBoton("Agregar", colorBoton, colorTextoBoton, new ImageIcon(getClass().getResource("/sistemas_inventario/resources/agregar.png")));
        JButton btnEliminar = crearBoton("Eliminar", colorBoton, colorTextoBoton, new ImageIcon(getClass().getResource("/sistemas_inventario/resources/borrar.png")));
        JButton btnGuardar = crearBoton("Guardar", colorBoton, colorTextoBoton, new ImageIcon(getClass().getResource("/sistemas_inventario/resources/guardarr.png")));
        JButton btnVentas = crearBoton("Ventas", colorBoton, colorTextoBoton, new ImageIcon(getClass().getResource("/sistemas_inventario/resources/ventass.png")));
        JButton btnDashboard = crearBoton("Dashboard", colorBoton, colorTextoBoton, new ImageIcon(getClass().getResource("/sistemas_inventario/resources/dashboard.png")));

        gbc.gridx = 0; gbc.gridy = 0; panelBotones.add(btnAgregar, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panelBotones.add(btnEliminar, gbc);
        gbc.gridx = 2; gbc.gridy = 0; panelBotones.add(btnGuardar, gbc);
        gbc.gridx = 3; gbc.gridy = 0; panelBotones.add(btnVentas, gbc);
        gbc.gridx = 4; gbc.gridy = 0; panelBotones.add(btnDashboard, gbc);

        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnGuardar.addActionListener(e -> {
            GestionArchivo.guardarProductos(productos);
            JOptionPane.showMessageDialog(this, "Productos guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        btnVentas.addActionListener(e -> new VentasFrame(productos, ventas, this).setVisible(true));
        btnDashboard.addActionListener(e -> {
            if (dashboard == null || !dashboard.isVisible()) {
                dashboard = new DashboardFrameGrafico(productos, ventas);
                dashboard.setVisible(true);
            } else {
                dashboard.actualizarDatos();
            }
        });
    }

    // Método auxiliar para crear botones personalizados con escalado de imagen
    private JButton crearBoton(String texto, Color fondo, Color textoColor, ImageIcon icono) {
        int anchoIcono = 24;
        int altoIcono = 24;

        if (icono != null) {
            Image imagenOriginal = icono.getImage();
            Image imagenEscalada = imagenOriginal.getScaledInstance(anchoIcono, altoIcono, Image.SCALE_SMOOTH);
            icono = new ImageIcon(imagenEscalada);
        }
        
        JButton boton = new JButton(texto, icono);
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setToolTipText("Haga clic para " + texto.toLowerCase());
        
        return boton;
    }

    public void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Producto p : productos) {
            modeloTabla.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getCantidad(), p.getPrecio()});
        }
        if (dashboard != null && dashboard.isVisible()) {
            dashboard.actualizarDatos();
        }
    }
    
    private void actualizarFiltro() {
        String texto = txtBuscar.getText().toLowerCase();
        modeloTabla.setRowCount(0);
        for (Producto p : productos) {
            if (p.getNombre().toLowerCase().contains(texto) || p.getCodigo().toLowerCase().contains(texto)) {
                modeloTabla.addRow(new Object[]{p.getCodigo(), p.getNombre(), p.getCantidad(), p.getPrecio()});
            }
        }
    }

    private void agregarProducto() {
        JTextField codigo = new JTextField();
        JTextField nombre = new JTextField();
        JTextField cantidad = new JTextField();
        JTextField precio = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(new TitledBorder("Detalles del Producto"));
        panel.add(new JLabel("Código:"));
        panel.add(codigo);
        panel.add(new JLabel("Nombre:"));
        panel.add(nombre);
        panel.add(new JLabel("Cantidad:"));
        panel.add(cantidad);
        panel.add(new JLabel("Precio:"));
        panel.add(precio);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Nuevo Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                if (codigo.getText().isEmpty() || nombre.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El código y el nombre no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int cant = Integer.parseInt(cantidad.getText());
                double prec = Double.parseDouble(precio.getText());
                
                if (cant < 0 || prec < 0) {
                     JOptionPane.showMessageDialog(this, "La cantidad y el precio no pueden ser negativos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                productos.add(new Producto(codigo.getText(), nombre.getText(), cant, prec));
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Producto agregado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos para Cantidad y Precio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este producto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productos.remove(fila);
                actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventarioFrame().setVisible(true));
    }
}
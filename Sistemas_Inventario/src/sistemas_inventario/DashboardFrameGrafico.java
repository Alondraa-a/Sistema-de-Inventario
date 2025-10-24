/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemas_inventario;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardFrameGrafico extends JFrame {
    private ArrayList<Producto> productos;
    private ArrayList<Venta> ventas;
    private DefaultCategoryDataset dataset;
    private JLabel lblTotalProductos, lblTotalVentas, lblTotalRecaudado, lblPocoStock;

    public DashboardFrameGrafico(ArrayList<Producto> productos, ArrayList<Venta> ventas) {
        this.productos = productos;
        this.ventas = ventas;

        setTitle("Dashboard - Librería");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(new Color(230, 240, 255)); //Fondo principal (azul muy claro)

        // Panel superior de estadísticas
        JPanel panelDatos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelDatos.setBackground(Color.WHITE);
        panelDatos.setBorder(BorderFactory.createTitledBorder("Resumen"));
        lblTotalProductos = new JLabel();
        lblTotalVentas = new JLabel();
        lblTotalRecaudado = new JLabel();
        lblPocoStock = new JLabel();
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        lblTotalProductos.setFont(fontLabel);
        lblTotalVentas.setFont(fontLabel);
        lblTotalRecaudado.setFont(fontLabel);
        lblPocoStock.setFont(fontLabel);
        panelDatos.add(lblTotalProductos);
        panelDatos.add(lblTotalVentas);
        panelDatos.add(lblTotalRecaudado);
        panelDatos.add(lblPocoStock);

        // Gráfico de stock y ventas
        dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Productos", "Producto", "Cantidad",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.GRAY);
        ChartPanel chartPanel = new ChartPanel(chart);

        // Botón cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(70, 130, 180));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.addActionListener(e -> dispose());

        add(panelDatos, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        add(btnCerrar, BorderLayout.SOUTH);

        actualizarDatos();
    }

    public void actualizarDatos() {
        int totalProductos = productos.stream().mapToInt(Producto::getCantidad).sum();
        lblTotalProductos.setText("Total de productos: " + totalProductos);

        lblTotalVentas.setText("Total de ventas: " + ventas.size());
        double totalRecaudado = ventas.stream().mapToDouble(Venta::getTotal).sum();
        lblTotalRecaudado.setText("Total recaudado: S/ " + totalRecaudado);

        StringBuilder lista = new StringBuilder();
for (Producto p : productos) {
    if (p.getCantidad() <= 2) { // umbral de poco stock
        if (lista.length() > 0) lista.append(", ");
        lista.append(p.getNombre()).append(" (").append(p.getCantidad()).append(")");
    }
}
String listaProductos = lista.length() > 0 ? lista.toString() : "Ninguno";

          lblPocoStock.setText("<html>Productos con poco stock:<br>" 
            + listaProductos.replaceAll(", ", "<br>") 
            + "</html>");
        dataset.clear();
        for (Producto p : productos) {
            dataset.addValue(p.getCantidad(), "Stock", p.getNombre());
        }
        for (Venta v : ventas) {
            for (Producto p : v.getProductos()) {
                dataset.addValue(p.getCantidad(), "Ventas", p.getNombre());
            }
        }
    }
}

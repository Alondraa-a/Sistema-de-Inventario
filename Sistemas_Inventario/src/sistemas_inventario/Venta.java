/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemas_inventario;

import java.util.ArrayList;

public class Venta {
    private String codigo;
    private String cliente;
    private ArrayList<Producto> productos;

    public Venta(String codigo, String cliente, ArrayList<Producto> productos) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.productos = productos;
    }

    Venta(String codigo, String nombre, double precio) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getCodigo() { return codigo; }
    public String getCliente() { return cliente; }
    public ArrayList<Producto> getProductos() { return productos; }

    public double getTotal() {
        return productos.stream().mapToDouble(p -> p.getCantidad() * p.getPrecio()).sum();
    }
  
}

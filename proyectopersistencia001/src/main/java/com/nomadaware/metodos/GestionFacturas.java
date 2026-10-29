package com.nomadaware.metodos;

// Importaciones necesarias 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.nomadaware.entities.Cliente;
import com.nomadaware.entities.Factura;
import com.nomadaware.entities.MetodoPago;
import com.nomadaware.entities.Producto; 
import com.nomadaware.entities.DetalleFactura; 
import jakarta.persistence.EntityManager;

public class GestionFacturas {

    // Instancia de GestionCRUD para manejar operaciones CRUD de Factura
    private static final GestionCRUD<Factura> repo = new GestionCRUD<>(Factura.class); 

    // CREATE Actualizado para incluir Producto y DetalleFactura
    public static Factura crearFactura(EntityManager em, LocalDate fecha, int clienteId, int metodoPagoId, int productoId, int cantidad) {
        em.getTransaction().begin();
        try {
            // Buscar el cliente
            Cliente cliente = em.find(Cliente.class, clienteId);
            if (cliente == null) {
                throw new IllegalArgumentException("El cliente con ID " + clienteId + " no existe.");
            }

            // Buscar el método de pago
            MetodoPago metodoPago = em.find(MetodoPago.class, metodoPagoId);
            if (metodoPago == null) {
                throw new IllegalArgumentException("El método de pago con ID " + metodoPagoId + " no existe.");
            }
            
            // Buscar el producto
            Producto producto = em.find(Producto.class, productoId);
            if (producto == null) {
                throw new IllegalArgumentException("El producto con ID " + productoId + " no existe.");
            }
            
            // Calcular el subtotal y total automáticamente
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            
            // Crear la factura asignando el subtotal como total
            Factura factura = new Factura(fecha, subtotal, cliente, metodoPago);
            em.persist(factura);
            
            // Crear y persistir el DetalleFactura en la misma transacción
            DetalleFactura detalle = new DetalleFactura(cantidad, precioUnitario, subtotal, factura, producto);
            em.persist(detalle);

            // Actualizar la lista de detalles en la factura
            em.getTransaction().commit();
            return factura;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // READ - Obtener todas las facturas
    public static List<Factura> leerFacturas(EntityManager em) {
        return repo.leerTodos(em);
    }

    // UPDATE - Actualizar una factura existente
    public static void actualizarFactura(EntityManager em, Factura factura) {
        repo.actualizar(em, factura); 
    }

    // DELETE - Eliminar una factura por su ID
    public static boolean eliminarFactura(EntityManager em, int id) {
        return repo.eliminar(em, id);
    }
}
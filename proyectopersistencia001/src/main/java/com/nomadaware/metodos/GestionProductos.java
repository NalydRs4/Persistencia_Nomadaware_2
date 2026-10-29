package com.nomadaware.metodos;

import java.math.BigDecimal;
import java.util.List;
import com.nomadaware.entities.Categoria;
import com.nomadaware.entities.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class GestionProductos {

    // Instancia genérica para delegar las operaciones estándar CRUD
    private static final GestionCRUD<Producto> repo = new GestionCRUD<>(Producto.class);

    // CREATE - Crear un nuevo producto
    public static Producto crearProducto(EntityManager em, String nombre, int cantidad, BigDecimal precio, String nombreCategoria) {
        em.getTransaction().begin();
        // Intentar buscar la categoría existente o crear una nueva si no existe
        try {
            Categoria categoria;
            try {
                categoria = em.createQuery("SELECT c FROM Categoria c WHERE c.descripcion = :desc", Categoria.class)
                        .setParameter("desc", nombreCategoria)
                        .getSingleResult();
            } catch (NoResultException e) {
                categoria = new Categoria(nombreCategoria);
                em.persist(categoria);
            }
            // Crear y persistir el producto con la categoría encontrada o creada
            Producto producto = new Producto(nombre, cantidad, precio, categoria);
            em.persist(producto);
            em.getTransaction().commit();
            return producto;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    // READ - Obtener todos los productos
    public static List<Producto> leerProductos(EntityManager em) {
        return repo.leerTodos(em);
    }

    // UPDATE - Actualizar el precio de un producto
    public static boolean actualizarPrecio(EntityManager em, int id, BigDecimal nuevoPrecio) {
        Producto producto = repo.buscarPorId(em, id);
        if (producto != null) {
            producto.setPrecio(nuevoPrecio);
            repo.actualizar(em, producto);
            return true;
        }
        return false;
    }
    
    // DELETE - Eliminar un producto por su ID
    public static boolean eliminarProducto(EntityManager em, int id) {
        return repo.eliminar(em, id);
    }
}
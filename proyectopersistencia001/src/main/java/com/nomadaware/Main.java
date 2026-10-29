package com.nomadaware;

// Importaciones necesarias para JPA
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        // Crear una fábrica de EntityManager utilizando la unidad de persistencia definida en persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("UnidadPersistencia");
        // Crear un EntityManager a partir de la fábrica
        EntityManager em = emf.createEntityManager();

        // Iniciar la interfaz de usuario del menú, pasando el EntityManager para que pueda interactuar con la base de datos
        MenuUI.iniciar(em);

        // Cerrar el EntityManager y la fábrica de EntityManager al finalizar la aplicación
        em.close();
        emf.close();
    }
}

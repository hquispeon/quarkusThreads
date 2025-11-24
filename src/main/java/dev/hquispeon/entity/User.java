package dev.hquispeon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA simple que representa un usuario.
 * - id: clave primaria Long auto-generada.
 * - name: nombre del usuario.
 *
 * Comentarios: Usamos las anotaciones estándar de JPA. Panache
 * funciona con entidades JPA normales.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    // Constructor por defecto requerido por JPA
    public User() {}

    public User(String name) {
        this.name = name;
    }

    // getters y setters (puedes generar con tu IDE)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

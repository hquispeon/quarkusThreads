package dev.hquispeon.repository;

import dev.hquispeon.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Repositorio Panache:
 * - Implementa PanacheRepository<User> para usar métodos convenientes como listAll(), findById(), persist().
 * - Annotated with @ApplicationScoped para inyección por CDI.
 *
 * Buenas prácticas: encapsular operaciones de BD aquí; el service se encarga de lógica.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Listar todos los usuarios.
     * Devuelve una List<User> usando Panache.
     */
    public List<User> listar() {
        return listAll();
    }

    /**
     * Encontrar por id.
     * Retornamos Optional para indicar ausencia de entidad.
     */
    public Optional<User> encontrarPorId(Long id) {
        return Optional.ofNullable(findById(id));
    }

    /**
     * Guardar usuario (persist).
     * Panache persist() es suficiente; si el objeto no tiene id se crea,
     * si tiene id y existe se actualiza (según comportamiento JPA).
     */
    public User guardar(User u) {
        persist(u);
        // persist no siempre sincroniza la id inmediatamente en modo flush diferido,
        // pero para H2 en memoria esto funcionará en dev.
        return u;
    }
}


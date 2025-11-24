package dev.hquispeon.service;

import dev.hquispeon.dto.UserDto;
import dev.hquispeon.entity.User;
import dev.hquispeon.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio: contiene lógica de negocio.
 * - Controladores deben ser delgados y delegar en servicios.
 * - Los servicios interactúan con repositorios.
 */
@ApplicationScoped
public class UserService {

    @Inject
    UserRepository repository;

    /**
     * Crear usuario.
     * @param name nombre
     * @return usuario persistido
     */
    @Transactional
    public User createUser(String name) {
        User u = new User(name);
        // repository.guardar está anotado con persist() (usa la transacción actual).
        return repository.guardar(u);
    }

    /**
     * Obtener todos los usuarios.
     */
    public List<User> getAllUsers() {
        return repository.listar();
    }

    /**
     * Obtener por id.
     */
    public Optional<User> getUser(Long id) {
        return repository.encontrarPorId(id);
    }
    
    /**
     * Update por id.
     */
    @Transactional
    public User updateUser(Long id, UserDto dto) {
        User user = repository.findById(id);

        if (user == null) {
            return null;
        }

        user.setName(dto.name()); // Hibernate detecta cambios (dirty checking)

        return user;
    }
    
    /**
     * Delete por id.
     */
    @Transactional
    public boolean deleteUser(Long id) {
        return repository.deleteById(id);
    }

}

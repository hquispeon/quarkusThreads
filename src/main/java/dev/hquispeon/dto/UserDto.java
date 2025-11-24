package dev.hquispeon.dto;

/**
 * DTO simple como record (Java 21).
 * - Usar DTOs evita exponer entidades JPA directamente en la API.
 */
public record UserDto(Long id, String name) {
    public static UserDto fromEntity(dev.hquispeon.entity.User u) {
        return new UserDto(u.getId(), u.getName());
    }
}
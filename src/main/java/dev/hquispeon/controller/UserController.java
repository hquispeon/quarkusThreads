package dev.hquispeon.controller;

import dev.hquispeon.dto.UserDto;
import dev.hquispeon.entity.User;
import dev.hquispeon.service.UserService;

import io.smallrye.common.annotation.RunOnVirtualThread;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource REST que expone endpoints /users.
 *
 * Importante: Quarkus soporta @RunOnVirtualThread (proporcionado por SmallRye).
 * - Esta anotación indica a Quarkus que el método será ejecutado en un nuevo Virtual Thread
 *   (si la JVM soporta virtual threads, p.ej. Java 21+).
 * - Esto permite escribir código síncrono (p. ej. usar JDBC bloqueante) sin bloquear
 *   el carrier/platform thread del event loop.
 *
 * Nota: Según la guía de Quarkus se recomienda usar @RunOnVirtualThread sólo en endpoints
 *       que sean "blocking" o que devuelvan tipos síncronos (no-reactivos).
 *       (ver docs oficiales). :contentReference[oaicite:2]{index=2}
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    /**
     * Crear usuario.
     * El método es síncrono (no retorna Uni/CompletionStage), por lo tanto
     * se ejecuta en un worker thread normalmente; con @RunOnVirtualThread lo ejecutamos en VT.
     */
    @POST
    @RunOnVirtualThread
    public Response create(UserDto dto) {
        // Lógica mínima en controlador: delegar todo al servicio.
        User created = userService.createUser(dto.name());
        return Response.status(Response.Status.CREATED)
                .entity(UserDto.fromEntity(created))
                .build();
    }

    /**
     * Listar todos los usuarios.
     * Ejecutado en Virtual Thread para demostrar integración con Loom.
     */
    @GET
    @RunOnVirtualThread
    public List<UserDto> listAll() {
        return userService.getAllUsers()
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuario por id.
     */
    @GET
    @Path("{id}")
    @RunOnVirtualThread
    public Response getById(@PathParam("id") Long id) {
        return userService.getUser(id)
                .map(u -> Response.ok(UserDto.fromEntity(u)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Endpoint práctico que simula una operación bloqueante (lenta).
     * - Thread.sleep(2000) simula bloqueo.
     * - En un Virtual Thread el carrier thread se "unmounts" y no queda bloqueado,
     *   por tanto la infraestructura mantiene mayor capacidad de concurrencia.
     *
     * Nota: esto es una simulación; en producción preferirás evitar Thread.sleep y usar APIs asíncronas
     * o drivers que no pinnen el carrier thread.
     */
    @GET
    @Path("slow")
    @RunOnVirtualThread
    public Response slow() {
        try {
            // Simula operación bloqueante (E/S lenta, llamada externa, etc.)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.serverError().build();
        }
        return Response.ok("finished slow operation").build();
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response updateUser(@PathParam("id") Long id, UserDto dto) {
        // Llamamos al servicio para actualizar
        var updated = userService.updateUser(id, dto);

        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.noContent().build(); // 204
    }

}

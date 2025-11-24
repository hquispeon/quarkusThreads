# quarkusThreads
quarkus Threads and project Loom
=======
# Quarkus + Loom POC (loompoc)

Proyecto de ejemplo que demuestra:
- Quarkus (Maven, Java openjdk 21)
- H2 (JDBC) + Hibernate ORM Panache
- Uso de Virtual Threads (Project Loom) con `@RunOnVirtualThread`
- SmallRye OpenAPI / Swagger UI

## Comentarios Properties
db-kind, jdbc.url, driver, username, password: configuran la conexión a H2 en memoria.
hibernate-orm.database.generation=update: crea/actualiza tablas según tus entidades.
quarkus.virtual-threads.enabled=true: activa la opción relacionada con virtual threads en Quarkus. Quarkus crea y administra virtual threads cuando ejecutas métodos con @RunOnVirtualThread.

# Comentarios Controller
Importante: la anotación @RunOnVirtualThread proviene de io.smallrye.common.annotation.RunOnVirtualThread (tal y como muestra la documentación Quarkus). Cuando Quarkus detecta Java 21+ creará un virtual thread para el método y lo ejecutará allí, evitando bloquear el carrier thread. Revisa la documentación oficial para consideraciones sobre pinning y monopolization (riesgos de usar VT en librerías que no son VT-friendly). 
Quarkus

## Estructura
- `com.example.loompoc.entity` - entidades JPA
- `com.example.loompoc.repository` - repositorios Panache
- `com.example.loompoc.service` - lógica de negocio
- `com.example.loompoc.controller` - controladores REST (uso de Virtual Threads)
- `src/main/resources/application.properties` - configuración

## Endpoints
- `POST /users` - crear usuario (body JSON `{ "id": null, "name": "Juan" }`)
- `GET  /users` - listar usuarios
- `GET  /users/{id}` - obtener por id
- `GET  /users/slow` - endpoint que simula operación bloqueante (`Thread.sleep(2000)`)

## Ejecutar en modo desarrollo
Requerimientos:
- Java openjdk 21 (recomendado)
- Maven 3.9+

Comandos:
```bash
mvn compile quarkus:dev
mvn quarkus:dev

Probar si ha levantado:
http://localhost:8080/
http://localhost:8080/q/swagger-ui/

## Ejecutar el JAR nativo/runner
mvn clean install
En bash
target/quarkusthreads-1.0.0-SNAPSHOT-runner.jar
java -jar target/quarkusthreads-1.0.0-SNAPSHOT-runner.jar

## Ejecutar la app nativa (si usas GraalVM Native Image)
mvn -Pnative clean install
./target/quarkusthreads-1.0.0-SNAPSHOT-runner

## CURLs de los endpoints del proyecto
Validador de endpoints  
curl -X GET http://localhost:8080/hello  

Crear un usuario  
curl -X POST http://localhost:8080/users \  
  -H "Content-Type: application/json" \  
  -d '{"name": "Juan"}'  

Obtener todos  
curl -X GET http://localhost:8080/users

Obtener por id  
curl -X GET http://localhost:8080/users/1

Actualizar  
curl -X PUT http://localhost:8080/users/1 \  
  -H "Content-Type: application/json" \  
  -d '{"name": "Nuevo Nombre"}'  

Eliminar  
curl -X DELETE http://localhost:8080/users/1

##Endpoint de demostración de Threads sin costo 
Es un endpoint usado para simular una operación lenta o bloqueante.  
Ejemplos reales:  
Consultar un servicio externo que demora  
Llamar a una BD pesada  
Leer un archivo grande  
Hacer una llamada HTTP bloqueante  
Cualquier proceso I/O que “duerme” o se detiene  

curl -X GET http://localhost:8080/users/slow  

Ejecuta este endpoint dentro de un Virtual Thread en vez de un Thread del sistema operativo  

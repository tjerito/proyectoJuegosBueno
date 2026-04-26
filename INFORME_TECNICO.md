# Informe tecnico - ProyectoJuegos

## 1. Descripcion del servicio
ProyectoJuegos es un servicio web desarrollado con Spring Boot y Vaadin para gestionar un catalogo de juegos y la interaccion de usuarios con ese catalogo. El backend expone API REST para:
- autenticacion y registro con JWT,
- gestion de usuarios,
- gestion de juegos y generos,
- resenas,
- biblioteca personal de juegos (`UserGame`),
- estadisticas administrativas.

Adicionalmente, el proyecto incluye vistas Vaadin para login/registro y acceso web (`LoginView`, `RegistrationView`, `MainView`).

## 2. Arquitectura del sistema
### Diagrama de capas (descripcion textual)
1. Capa de presentacion:
   - REST Controllers en `src/main/java/com/example/proyectoJuegos/Controllers`.
   - Vistas Vaadin en `src/main/java/com/example/proyectoJuegos/Views`.
2. Capa de seguridad:
   - `SecurityConfig`, `JwtAuthFilter`, `JwtAuthEntryPoint`, `JwtUtils`.
3. Capa de negocio:
   - Servicios en `src/main/java/com/example/proyectoJuegos/Services`.
4. Capa de persistencia:
   - Repositorios Spring Data JPA en `src/main/java/com/example/proyectoJuegos/Repositories`.
5. Capa de dominio:
   - Entidades JPA en `src/main/java/com/example/proyectoJuegos/Entities`.

### Stack tecnologico con versiones
- Java 21
- Spring Boot 3.5.9
- Spring Security (starter de Spring Boot)
- Spring Data JPA (starter de Spring Boot)
- Vaadin 24.9.9
- H2 Database (runtime)
- JWT (`io.jsonwebtoken:jjwt-*`) 0.11.5
- OpenAPI/Swagger UI (`springdoc-openapi-starter-webmvc-ui`) 2.8.9
- JUnit 5 + Spring Boot Test + Spring Security Test

## 3. Modelo de dominio
### Entidades y relaciones
- `Usuario`
  - Datos de cuenta: nombre, email unico, password cifrada, fechaCreacion.
  - Roles: `Set<Role>` (`USER`, `ADMIN`).
  - Relacion 1:N con `UserGame` (`lista`).
- `Juego`
  - Datos: titulo, descripcion, fechaSalida, urlImagen.
  - Relacion N:M con `Genero`.
  - Relacion 1:N con `Review`.
  - Relacion 1:N con `UserGame`.
- `Genero`
  - Campo `nombre` unico.
- `Review`
  - Datos: comentario, rating (1-5), fechaReview.
  - Relacion N:1 con `Usuario` (autor).
  - Relacion N:1 con `Juego`.
- `UserGame`
  - Datos: estado (`PENDIENTE`, `JUGANDO`, `COMPLETADO`), horasJugadas, rating, fechaAdicion.
  - Relacion N:1 con `Usuario`.
  - Relacion N:1 con `Juego`.

## 4. Endpoints REST documentados
| Metodo | Ruta | Descripcion | Autenticacion requerida | Rol requerido |
|---|---|---|---|---|
| POST | `/api/auth/registro` | Registro de usuario (opcional admin con `X-Admin-Secret`) y emision de JWT | No | No |
| POST | `/api/auth/login` | Login con email/password y emision de JWT | No | No |
| GET | `/api/juegos` | Listar juegos | Si (JWT) | USER/ADMIN |
| GET | `/api/juegos/{id}` | Obtener juego por id | Si (JWT) | USER/ADMIN |
| GET | `/api/juegos/buscar?nombre=` | Buscar juegos por nombre parcial | Si (JWT) | USER/ADMIN |
| GET | `/api/juegos/novedades` | Top 5 novedades | Si (JWT) | USER/ADMIN |
| GET | `/api/juegos/recientes?fecha=` | Juegos lanzados desde fecha | Si (JWT) | USER/ADMIN |
| POST | `/api/juegos` | Crear juego | Si (JWT) | ADMIN |
| GET | `/api/usuarios` | Listar usuarios | Si (JWT) | USER/ADMIN |
| GET | `/api/usuarios/{id}` | Obtener usuario por id | Si (JWT) | USER/ADMIN |
| GET | `/api/usuarios/email?valor=` | Obtener usuario por email | Si (JWT) | USER/ADMIN |
| POST | `/api/usuarios` | Crear usuario | Si (JWT) | USER/ADMIN |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario | Si (JWT) | ADMIN |
| GET | `/api/reviews` | Listar resenas | Si (JWT) | USER/ADMIN |
| GET | `/api/reviews/juego/{juegoId}` | Listar resenas por juego | Si (JWT) | USER/ADMIN |
| GET | `/api/reviews/autor/{usuarioId}` | Listar resenas por autor | Si (JWT) | USER/ADMIN |
| GET | `/api/reviews/destacadas?min=` | Filtrar resenas por nota minima | Si (JWT) | USER/ADMIN |
| POST | `/api/reviews` | Crear resena | Si (JWT) | USER/ADMIN |
| DELETE | `/api/reviews/{id}` | Eliminar resena | Si (JWT) | ADMIN |
| GET | `/api/generos` | Listar generos | Si (JWT) | USER/ADMIN |
| GET | `/api/generos/{id}` | Obtener genero por id | Si (JWT) | USER/ADMIN |
| GET | `/api/generos/buscar?nombre=` | Buscar genero por nombre | Si (JWT) | USER/ADMIN |
| POST | `/api/generos` | Crear/actualizar genero | Si (JWT) | ADMIN |
| DELETE | `/api/generos/{id}` | Eliminar genero | Si (JWT) | ADMIN |
| GET | `/api/biblioteca/usuario/{userId}` | Ver biblioteca de usuario | Si (JWT) | USER/ADMIN |
| POST | `/api/biblioteca` | Crear/actualizar progreso de biblioteca | Si (JWT) | USER/ADMIN |
| PATCH | `/api/biblioteca/usuario/{userId}/juego/{gameId}/horas?cantidad=` | Anadir horas jugadas | Si (JWT) | USER/ADMIN |
| PUT | `/api/biblioteca/usuario/{userId}/juego/{gameId}/estado` | Cambiar estado de un juego | Si (JWT) | USER/ADMIN |
| GET | `/api/biblioteca/filtro?estado=` | Filtrar biblioteca por estado | Si (JWT) | USER/ADMIN |
| GET | `/api/admin/estadisticas` | Totales y juego mas resenado | Si (JWT) | ADMIN |

Rutas publicas de documentacion:
- `/api/docs`
- `/api/docs/**`
- `/v3/api-docs/**`
- `/swagger-ui/**`
- `/swagger-ui.html`

## 5. Medidas de seguridad implementadas
### Autenticacion JWT (flujo completo)
1. El usuario se registra (`/api/auth/registro`) o inicia sesion (`/api/auth/login`).
2. El backend genera JWT firmado con `HS512` (`JwtUtils.generarToken`).
3. El cliente envia `Authorization: Bearer <token>` en llamadas `/api/**`.
4. `JwtAuthFilter` valida firma/expiracion y carga el usuario con `AuthenticatedUser`.
5. Si no hay token valido, `JwtAuthEntryPoint` responde `401` JSON.

### Control de roles ADMIN/USER
- Seguridad global:
  - `/api/auth/**` publico.
  - resto de `/api/**` autenticado.
- Seguridad por metodo con `@PreAuthorize`:
  - ADMIN requerido en altas/bajas sensibles (`/api/juegos` POST, `/api/usuarios/{id}` DELETE, `/api/reviews/{id}` DELETE, `/api/generos` POST/DELETE, `/api/admin/estadisticas`).

### Cifrado de contrasenas BCrypt
- `SecurityConfig` expone `PasswordEncoder` como `BCryptPasswordEncoder`.
- En registro se cifra password antes de guardar (`AuthController`).
- En login se valida con `passwordEncoder.matches(...)`.

### Proteccion contra XSS, headers de seguridad
En `SecurityConfig` se configuran:
- cabecera X-XSS-Protection en modo bloqueo,
- Content-Security-Policy,
- `frameOptions().deny()`.

### Rate limiting en login
`AuthController` implementa limitacion por IP:
- maximo 5 intentos fallidos,
- ventana de 15 minutos,
- bloqueo con respuesta HTTP `429`.

## 6. Pruebas realizadas
### Tests unitarios de servicios
- `UsuarioServiceTest`
- `JuegoServiceTest`
- `ReviewServiceTest`
- `UserGameServiceTest`

### Tests de controladores
- `AuthControllerTest`
- `JuegoControllerTest`
- `UsuarioControllerTest`

### Pruebas de acceso no autorizado
- `SecurityIntegrationTest` valida:
  - acceso sin token (`401`),
  - token manipulado (`401`),
  - token expirado (`401`),
  - accesibilidad de rutas publicas (`/api/auth/*`, `/v3/api-docs`, `/swagger-ui.html`).

## 7. Instrucciones de despliegue
1. Compilar artefacto:
   - `mvn clean package -DskipTests`
2. Construir imagen:
   - `docker build -t proyecto-juegos .`
3. Ejecutar contenedor:
   - `docker run -p 8080:8080 proyecto-juegos`
4. Acceder a documentacion:
   - `http://localhost:8080/api/docs`

Configuracion de produccion:
- Perfil `prod` en `application-prod.properties`.
- H2 persistente en archivo: `jdbc:h2:file:./data/juegosdb`.
- Consola H2 desactivada.
- Nivel de log raiz en `WARN`.

## 8. Conclusiones y mejoras futuras
Conclusiones:
- El proyecto dispone de una arquitectura en capas clara con seguridad JWT y control de roles.
- Se incorporaron pruebas de controladores y seguridad para cubrir autenticacion, autorizacion y rutas publicas.
- El despliegue queda preparado para un entorno controlado con Docker y persistencia local en archivo.

Mejoras futuras recomendadas:
- Migrar de H2 a un motor de base de datos de produccion (PostgreSQL/MySQL).
- Externalizar secretos (`jwt.secret`, `admin.secret`) en variables de entorno/secret manager.
- Anadir observabilidad (health checks, metricas, trazas) y pipeline CI/CD con analisis de seguridad.
- Ampliar cobertura de tests de integracion para el resto de controladores (`Review`, `Genero`, `UserGame`, `Admin`).



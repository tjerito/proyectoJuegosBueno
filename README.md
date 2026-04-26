# ProyectoJuegos

Aplicacion Java Spring Boot + Vaadin para gestion de juegos, usuarios, resenas, generos y biblioteca personal.

## Despliegue con Docker

### 1) Construir el JAR

```bash
mvn clean package -DskipTests
```

### 2) Construir la imagen Docker

```bash
docker build -t proyecto-juegos .
```

### 3) Ejecutar el contenedor

```bash
docker run -p 8080:8080 proyecto-juegos
```

La aplicacion arranca con el perfil `prod` definido en el `Dockerfile`.

### 4) Acceso a Swagger UI

- URL: `http://localhost:8080/api/docs`

## Opcion con Docker Compose

```bash
docker compose up --build
```

Este compose publica el puerto `8080` y monta `./data` en `/app/data` para persistir la base H2 en archivo.



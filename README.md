# Inventra

Pequeña API de inventario y ventas desarrollada en Spring Boot.

Contenido del README:
- Descripción
- Requisitos
- Ejecutar local
- Endpoints principales (Products, Categories, Sales, Clients)
- Ejemplos de curl (incluye paginación y búsqueda por nombre/fecha)

Requisitos
- Java 17+
- Maven

Ejecutar local
En Windows (cmd.exe):

mvnw.cmd spring-boot:run

Endpoints

1) Products
- POST /products/create
  - Crea un producto.
  - Payload (JSON):
    {
      "name": "Mouse",
      "description": "Wireless mouse",
      "price": 12.5,
      "image": "http://...",
      "quantity": 10,
      "category": { "id": 1 },
      "brand": "Logi",
      "status": "ACTIVE"
    }

- GET /products/list
  - Lista todos los productos.

- GET /products/search?name={name}&page={page}&size={size}
  - Busca productos por nombre (coincidencia parcial). Si `name` es vacío o no se provee, retorna todos paginados.
  - Validaciones: page >= 0, size > 0 y <= 100
  - Ejemplo curl (buscar "Mou", página 0, tamaño 10):

    curl -X GET "http://localhost:8080/products/search?name=Mou&page=0&size=10" -H "Accept: application/json"

- GET /products/{productId}
  - Obtener un producto por id.

- PUT /products/update
  - Actualiza un producto. Payload igual que create e incluye el campo `id`.

- DELETE /products/{productId}
  - Elimina un producto.

2) Categories
- POST /category/create
  - Crear categoría. Payload:
    {
      "name": "Periféricos",
      "code": "PER",
      "description": "...",
      "status": "ACTIVE",
      "icon": ""
    }

- GET /category/list
  - Lista todas las categorías.

- GET /category/search?name={name}&page={page}&size={size}
  - Busca categorías por nombre (parcial). Si `name` vacío retorna todo paginado.
  - Validaciones: page >=0, size >0 y <=100
  - Ejemplo curl:

    curl -X GET "http://localhost:8080/category/search?name=Peri&page=0&size=10" -H "Accept: application/json"

- GET /category/{categoryId}
- PUT /category/update
- DELETE /category/{categoryId}

3) Sales
- POST /sales/create
  - Crea una venta. Payload de ejemplo:
    {
      "total": 37.5,
      "status": "COMPLETED",
      "client": { "id": 1 },
      "productList": [ { "id": 2, "quantity": 3 }, { "id": 4, "quantity": 1 } ]
    }

- GET /sales/list?date={yyyy-MM-dd}&page={page}&size={size}
  - Lista ventas paginadas. `date` es opcional y filtra por fecha (formato simple yyyy-MM-dd).
  - Validaciones: page >= 0, size > 0
  - Ejemplo curl:

    curl -X GET "http://localhost:8080/sales/list?date=2025-11-01&page=0&size=10" -H "Accept: application/json"

- GET /sales/detail/{id}
  - Devuelve detalle de la venta incluyendo los productos vendidos y su cantidad.
  - Ejemplo curl:

    curl -X GET "http://localhost:8080/sales/detail/1" -H "Accept: application/json"

4) Clients
- POST /clients/create
- GET /clients/list?name={name}&page={page}&size={size}
  - Lista clientes paginados y busca por nombre si se provee.
  - Ejemplo curl:

    curl -X GET "http://localhost:8080/clients/list?name=Juan&page=0&size=10" -H "Accept: application/json"

Notas
- Los endpoints devuelven una estructura de respuesta estándar en `ResponseHandler`.
- Validaciones de `page` y `size` están implementadas en los controladores.

Contacto
- Proyecto generado por el equipo de Inventra


Base de datos

- Motor por defecto (perfil `local`): PostgreSQL
- Configuración por defecto (ver `src/main/resources/application-local.properties`):
  - URL: jdbc:postgresql://localhost:5432/inventra_bd
  - Usuario: postgres
  - Contraseña: 0000
  - Driver: org.postgresql.Driver
  - Pool: HikariCP (maximum-pool-size=10, minimum-idle=2, connection-timeout=30000, idle-timeout=30000)
  - JPA/Hibernate: spring.jpa.hibernate.ddl-auto=update (actualiza el esquema automáticamente en desarrollo)
  - Puerto del servidor en perfil local: 8081 (property `server.port`)

- Nota de seguridad: No dejar credenciales en repositorios para entornos de producción; usar variables de entorno, archivos de configuración no versionados o gestores de secretos.

- Ejecutar la aplicación con el perfil `local` (Windows/cmd.exe):

```cmd
cd "C:\Users\FMSP\Documents\Idea Projects\inventra"
mvnw.cmd -Dspring-boot.run.profiles=local spring-boot:run
```

- Ejecutar con variables de entorno para sobreescribir la conexión (ejemplo en cmd.exe):

```cmd
set SPRING_DATASOURCE_URL=jdbc:postgresql://dbhost:5432/mi_db&& set SPRING_DATASOURCE_USERNAME=mi_user&& set SPRING_DATASOURCE_PASSWORD=mi_pass&& mvnw.cmd spring-boot:run
```

- Recomendación: en desarrollo instala PostgreSQL localmente y crea la base `inventra_bd` o cambia la URL a la base que tengas. Asegúrate de que el driver `org.postgresql:postgresql` esté en el `pom.xml` (normalmente ya incluido en proyectos Spring Boot con dependencia JDBC/Postgres).

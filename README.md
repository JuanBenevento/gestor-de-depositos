# Proyecto Final - Programación III - UTN FRMDP

Este proyecto es parte de la materia **Programación III** de la Universidad Tecnológica Nacional - Facultad Regional Mar del Plata. Desarrollado con **Java Spring Boot** y **MySQL** como base de datos relacional.

## Tecnologías utilizadas

- Java 23+
- Spring Boot 3+
- Maven
- MySQL 8.x
- JPA / Hibernate
- Postman (para pruebas y documentación de request)
- Lombok (para reducir código boilerplate)

---

## Requisitos previos

Antes de compilar y ejecutar el proyecto, asegurate de tener instalado:

- **JDK 23** o superior
- **Maven 4.0+**
- **MySQL 8+**
- Un entorno como **IntelliJ IDEA** (opcional pero recomendado)

---

## Configuración de la base de datos

1. Crear una base de datos en MySQL:

```sql
CREATE DATABASE GestorDeDepositos;
```

2. Configurar los datos de conexión en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/GestorDeDepositos?useSSL=false&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.sql.init.mode=never
```

> Podés usar `ddl-auto=create` si querés que la base de datos se genere desde cero cada vez (solo recomendable en desarrollo inicial).

---

## Compilación y ejecución

1. Clonar el repositorio:
```bash
git clone https://github.com/JuanBenevento/gestor-de-depositos.git
cd tu_repositorio
```

2. Compilar el proyecto con Maven:
```bash
mvn clean install
```

3. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```
---

## Pruebas

Las rutas del API se pueden probar usando herramientas como **Postman** o **curl**. Por ejemplo:

```bash
GET http://localhost:8080/api/ubicaciones
POST http://localhost:8080/api/ubicaciones
```

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/juan/curso/springboot/webapp/gestordedepositos
│   │       ├── Controladores/
│   │       ├── Dtos/
│   │       ├── Repositorios/
│   │       ├── Modelos/
│   │       └── Servicios/
│   └── resources/
│       ├── application.properties
│       └── schema.sql (opcional)
```

---

## Créditos

Proyecto desarrollado por Juan Manuel Benevento, Lucia Saederup y Lautar Toledo.
Programación III – UTN Facultad Regional Mar del Plata
Daniel Diaz - Eduardo Mango

Año: 2025

---

## Licencia

Este proyecto es de uso académico y no tiene fines comerciales.

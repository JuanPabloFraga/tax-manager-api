# Tax Manager API

API REST de gestión fiscal argentina para contadores independientes.

Permite administrar contribuyentes (clientes), registrar comprobantes de compra/venta
(facturas, notas de crédito/débito), y generar libros IVA por período fiscal.

---

## Stack Tecnológico

| Componente     | Tecnología                          |
|----------------|-------------------------------------|
| Lenguaje       | Java 21 LTS                        |
| Framework      | Spring Boot 4.0.x                   |
| Build          | Maven                               |
| Base de datos  | PostgreSQL 16                       |
| ORM            | Spring Data JPA (Hibernate)         |
| Migraciones    | Flyway                              |
| Autenticación  | JWT (jjwt) — access + refresh token |
| Documentación  | SpringDoc OpenAPI (Swagger)         |
| Mapping        | MapStruct                           |
| Testing        | JUnit 5 + Mockito + Testcontainers  |
| CI/CD          | GitHub Actions                      |
| Contenedores   | Docker + Docker Compose             |

## Arquitectura

**Hexagonal (Ports & Adapters)** pragmática, organizada por bounded contexts:

```
src/main/java/com/.../taxmanagerapi/
├── auth/          → Autenticación y autorización (JWT, roles)
├── taxpayer/      → CRUD de contribuyentes (CUIT, condición fiscal)
├── voucher/       → Registro de comprobantes (facturas, NC, ND)
├── vatbook/       → Generación de libros IVA (query-only)
└── shared/        → Excepciones, seguridad, config, validadores fiscales
```

Cada bounded context tiene internamente: `domain/` → `application/` → `infrastructure/`.

Ver [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) para diagramas y detalles completos.

## Endpoints

| Método   | Endpoint                                 | Descripción                    |
|----------|------------------------------------------|--------------------------------|
| `POST`   | `/api/v1/auth/register`                  | Registrar usuario              |
| `POST`   | `/api/v1/auth/login`                     | Iniciar sesión                 |
| `POST`   | `/api/v1/auth/refresh`                   | Renovar access token           |
| `POST`   | `/api/v1/auth/logout`                    | Cerrar sesión                  |
| `POST`   | `/api/v1/taxpayers`                      | Crear contribuyente            |
| `GET`    | `/api/v1/taxpayers`                      | Listar contribuyentes          |
| `GET`    | `/api/v1/taxpayers/{id}`                 | Detalle de contribuyente       |
| `PUT`    | `/api/v1/taxpayers/{id}`                 | Actualizar contribuyente       |
| `DELETE` | `/api/v1/taxpayers/{id}`                 | Desactivar contribuyente       |
| `GET`    | `/api/v1/taxpayers/search?cuit=...`      | Buscar por CUIT                |
| `POST`   | `/api/v1/taxpayers/{id}/vouchers`        | Crear comprobante              |
| `GET`    | `/api/v1/taxpayers/{id}/vouchers`        | Listar comprobantes            |
| `GET`    | `/api/v1/vouchers/{id}`                  | Detalle de comprobante         |
| `GET`    | `/api/v1/vat-books/purchases?period=...` | Libro IVA Compras              |
| `GET`    | `/api/v1/vat-books/sales?period=...`     | Libro IVA Ventas               |

Ver [docs/API_SPEC.md](docs/API_SPEC.md) para request/response de ejemplo.

## Requisitos Previos

- **Java 21** (JDK)
- **Maven 3.9+**
- **Docker** y **Docker Compose** (para PostgreSQL y deployment)
- **Git**

## Inicio Rápido

### Con Docker Compose (recomendado)

```bash
# Clonar el repositorio
git clone https://github.com/{usuario}/tax-manager-api.git
cd tax-manager-api

# Levantar la aplicación + PostgreSQL
docker compose up -d

# La API estará disponible en http://localhost:8080
# Swagger UI en http://localhost:8080/swagger-ui.html
```

### Desarrollo local

```bash
# 1. Levantar solo PostgreSQL con Docker
docker compose up -d postgres

# 2. Compilar y ejecutar
./mvnw spring-boot:run

# 3. Ejecutar tests
./mvnw test

# 4. Ejecutar tests de integración (requiere Docker para Testcontainers)
./mvnw verify
```

### Variables de entorno

| Variable                          | Default              | Descripción                        |
|-----------------------------------|----------------------|------------------------------------|
| `SPRING_DATASOURCE_URL`          | `jdbc:postgresql://localhost:5432/taxmanager` | URL de la BD |
| `SPRING_DATASOURCE_USERNAME`     | `taxmanager`         | Usuario de la BD                   |
| `SPRING_DATASOURCE_PASSWORD`     | `taxmanager`         | Contraseña de la BD                |
| `JWT_SECRET`                     | —                    | Clave secreta para firmar JWTs     |
| `JWT_ACCESS_TOKEN_EXPIRATION`    | `900000`             | Expiración access token (ms)       |
| `JWT_REFRESH_TOKEN_EXPIRATION`   | `604800000`          | Expiración refresh token (ms)      |

## Estructura del Proyecto

```
tax-manager-api/
├── docs/
│   ├── DOMAIN_GLOSSARY.md      → Diccionario fiscal → nombres en código
│   ├── DATA_MODEL.md           → Entidades, relaciones, diagrama ER
│   ├── API_SPEC.md             → Endpoints con request/response de ejemplo
│   ├── ARCHITECTURE.md         → Arquitectura hexagonal con diagramas
│   └── adr/                    → Architecture Decision Records
│       ├── 001-arquitectura-hexagonal-por-paquetes.md
│       ├── 002-always-valid-domain.md
│       ├── 003-dtos-separados-por-capa.md
│       ├── 004-jwt-con-refresh-token.md
│       ├── 005-entidad-jpa-es-entidad-dominio.md
│       └── 006-uuid-como-identificador.md
├── src/
│   ├── main/java/com/.../taxmanagerapi/
│   │   ├── auth/               → Bounded context: autenticación
│   │   ├── taxpayer/           → Bounded context: contribuyentes
│   │   ├── voucher/            → Bounded context: comprobantes
│   │   ├── vatbook/            → Bounded context: libros IVA
│   │   └── shared/             → Código transversal
│   ├── main/resources/
│   │   ├── application.yml
│   │   └── db/migration/       → Migraciones Flyway
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── PRODUCT_VISION.md           → Visión del producto y funcionalidades
├── CONTRIBUTING.md             → Convenciones del proyecto
├── CHANGELOG.md                → Historial de cambios
└── README.md                   → Este archivo
```

## Documentación

| Documento                                            | Contenido                                    |
|------------------------------------------------------|----------------------------------------------|
| [PRODUCT_VISION.md](PRODUCT_VISION.md)               | Problema, solución, funcionalidades, roadmap |
| [docs/DOMAIN_GLOSSARY.md](docs/DOMAIN_GLOSSARY.md)   | Términos fiscales → nombres en código        |
| [docs/DATA_MODEL.md](docs/DATA_MODEL.md)             | Modelo de datos, diagrama ER, constraints    |
| [docs/API_SPEC.md](docs/API_SPEC.md)                 | Endpoints con ejemplos JSON                  |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)         | Arquitectura, patrones, flujo de requests    |
| [docs/adr/](docs/adr/)                               | Decisiones arquitectónicas (ADRs)            |
| [CONTRIBUTING.md](CONTRIBUTING.md)                    | Convenciones de código, commits, testing     |
| [CHANGELOG.md](CHANGELOG.md)                         | Historial de cambios                         |

## Tests

**83 unit tests** — todos pasando ✅

| Módulo    | Tests | Cobertura                                  |
|-----------|-------|--------------------------------------------|
| shared    | 26    | CuitValidator (21 parameterized), JwtProvider (5) |
| taxpayer  | 19    | Entidad (13), CreateService (2), GetService (4) |
| voucher   | 16    | Entidad (9), CreateService (2), GetService (5) |
| vatbook   | 3     | GetVatBookService (3)                      |
| auth      | 18    | User entity (12), RegisterService (3), LoginService (3) |
| app       | 1     | ApplicationContext startup                 |

```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests de un módulo específico
./mvnw test -Dtest="**/taxpayer/**"
```

## Licencia

Este proyecto está bajo la licencia especificada en [LICENSE](LICENSE).

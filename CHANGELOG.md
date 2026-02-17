# Changelog

Todos los cambios notables de este proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/),
y este proyecto adhiere a [Versionado Semántico](https://semver.org/lang/es/).

## [Sin publicar]

### Agregado

#### Documentación (Fase 0)
- Product Vision, Domain Glossary, Data Model, API Spec, Architecture
- 6 ADRs: hexagonal por paquetes, always valid domain, DTOs separados por capa,
  JWT con refresh token, entidad JPA = entidad dominio, UUID como identificador
- CONTRIBUTING.md con convenciones de código, commits, testing y SQL
- README.md con stack, arquitectura, endpoints, quick start y estructura

#### Scaffolding (Fase 0)
- `pom.xml` con Java 21, Spring Boot 4.0.0, PostgreSQL, H2, Flyway, jjwt, SpringDoc,
  MapStruct, Lombok, Mockito, Testcontainers
- `application.yml` y `application-test.yml` (H2 in-memory para tests)
- `Dockerfile` multi-stage y `docker-compose.yml`
- GitHub Actions CI (`ci.yml`) con Maven verify + perfil test
- `.gitignore`

#### shared/ (Fase 1)
- Jerarquía de excepciones: `DomainException` (abstract), `DomainValidationException` (422),
  `BadRequestException` (400), `ResourceNotFoundException` (404), `ConflictException` (409),
  `UnauthorizedException` (401)
- `GlobalExceptionHandler` — `@RestControllerAdvice` con RFC 7807 ProblemDetail
- `CuitValidator` — validación de CUIT argentino (módulo 11), formateo y strip
- `OpenApiConfig` — metadata Swagger + esquema Bearer auth
- Migraciones Flyway: V1 (users), V2 (refresh_tokens), V3 (taxpayers), V4 (vouchers)

#### taxpayer/ (Fase 2)
- Entidad `Taxpayer` — Always Valid Domain con factory method `create()`, `updateInfo()`,
  `deactivate()`, validación de CUIT módulo 11
- Enum `TaxCondition` (RESPONSABLE_INSCRIPTO, MONOTRIBUTISTA, EXENTO, CONSUMIDOR_FINAL,
  NO_RESPONSABLE)
- Puerto `TaxpayerRepository`, Spring Data JPA repo + adapter
- 4 DTOs de aplicación (records), 4 use case interfaces (CQRS lite), 4 services
- 4 DTOs web, `TaxpayerWebMapper`, `TaxpayerController` (6 endpoints)
- 19 unit tests (entidad + services)

#### voucher/ (Fase 3)
- Entidad `Voucher` — Always Valid con validación `net + vat + exempt = total`,
  montos no negativos, total > 0, punto de venta 1-99999
- Enums `VoucherCategory` (PURCHASE, SALE) y `VoucherType` (FACTURA_A/B/C,
  NOTA_CREDITO_A/B/C, NOTA_DEBITO_A/B/C, RECIBO, TICKET)
- Puerto `VoucherRepository` con query para libros IVA por categoría y período
- 3 DTOs aplicación, 2 use cases, 2 services, Spring Data repo + adapter
- 3 DTOs web, `VoucherWebMapper`, `VoucherController` (3 endpoints)
- 16 unit tests (entidad + services)

#### vatbook/ (Fase 4)
- BC query-only sin entidad de dominio propia
- `GetVatBookService` — calcula totales, carga taxpayers en batch (evita N+1)
- 3 DTOs aplicación (VatBookEntryResult, VatBookTotalsResult, VatBookResult)
- 3 DTOs web, `VatBookWebMapper`, `VatBookController` (2 endpoints: purchases + sales)
- 3 unit tests

#### auth/ + security/ (Fase 5)
- Entidad `User` — Always Valid, factory `create()`, `deactivate()`, email normalizado,
  rol default ACCOUNTANT
- Entidad `RefreshToken` — `create()`, `isExpired()`, `isUsable()`, `revoke()`
- Enum `UserRole` (ADMIN, ACCOUNTANT, VIEWER)
- 2 puertos de repositorio, Spring Data repos + adapters
- 4 DTOs aplicación, 4 use case interfaces, 4 services (Register, Login, RefreshToken, Logout)
- `JwtProvider` — genera/valida JWT HS256 con jjwt 0.12.6 (claims: sub, email, role)
- `JwtAuthenticationFilter` — `OncePerRequestFilter`, extrae Bearer token, setea SecurityContext
- `CustomAuthenticationEntryPoint` — 401 ProblemDetail JSON
- `CustomAccessDeniedHandler` — 403 ProblemDetail JSON
- `SecurityConfig` actualizado: JWT filter, entry point, access denied handler,
  `/api/v1/auth/**` público, resto autenticado
- 7 DTOs web, `AuthWebMapper`, `AuthController` (4 endpoints: register, login, refresh, logout)
- 23 unit tests (User entity, RegisterService, LoginService, JwtProvider)

#### Tests totales
- **83 unit tests**, 0 fallos, BUILD SUCCESS
- Cobertura: entidades de dominio, services de aplicación, JwtProvider
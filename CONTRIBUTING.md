# Guía de Contribución — Tax Manager API

Convenciones y reglas del proyecto para mantener consistencia en el código,
los commits y la documentación.

---

## Idiomas

| Elemento            | Idioma   | Ejemplo                                      |
|---------------------|----------|----------------------------------------------|
| Código (clases, variables, métodos) | Inglés | `Taxpayer`, `findByCuit()`, `netAmount` |
| Documentación (docs/, ADRs)         | Español | "Arquitectura hexagonal por paquetes"   |
| Commits y PRs                       | Español | "feat: agregar CRUD de contribuyentes"  |
| Comentarios en código               | Inglés  | `// Validate CUIT checksum digit`       |
| Mensajes de error al usuario        | Español | "El CUIT no es válido"                  |
| Logs de aplicación                  | Inglés  | `"Taxpayer created: {}"`                |

---

## Convenciones de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/es/) en español:

```
<tipo>: <descripción corta>

[cuerpo opcional]
```

### Tipos permitidos

| Tipo       | Uso                                          | Ejemplo                                      |
|------------|----------------------------------------------|----------------------------------------------|
| `feat`     | Nueva funcionalidad                          | `feat: agregar endpoint de búsqueda por CUIT`|
| `fix`      | Corrección de bug                            | `fix: corregir validación módulo 11`         |
| `docs`     | Solo documentación                           | `docs: agregar ADR-003`                      |
| `refactor` | Cambio de código sin cambio funcional        | `refactor: extraer CuitValidator a shared`   |
| `test`     | Agregar o modificar tests                    | `test: agregar unit tests de Taxpayer`       |
| `chore`    | Tareas de mantenimiento (CI, deps, config)   | `chore: actualizar dependencias de Spring`   |
| `style`    | Formateo, imports (sin cambio funcional)     | `style: ordenar imports en TaxpayerService`  |

### Reglas

- Descripción en **minúsculas**, sin punto final.
- Máximo **72 caracteres** en la primera línea.
- Si el commit afecta un módulo específico, se puede agregar scope:
  `feat(taxpayer): agregar validación de condición fiscal`

---

## Branching Strategy

```
main ← rama principal (siempre deployable)
 └── develop ← integración
      ├── feature/taxpayer-crud
      ├── feature/voucher-validation
      ├── fix/cuit-checksum
      └── chore/docker-compose
```

| Rama                    | Propósito                           | Se mergea a  |
|-------------------------|-------------------------------------|--------------|
| `main`                  | Producción, siempre estable         | —            |
| `develop`               | Integración de features             | `main`       |
| `feature/{descripcion}` | Nueva funcionalidad                 | `develop`    |
| `fix/{descripcion}`     | Corrección de bug                   | `develop`    |
| `chore/{descripcion}`   | Mantenimiento, CI, config           | `develop`    |

---

## Estructura de Código

### Paquetes

Referencia completa en [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

```
com.{group}.taxmanagerapi.{boundedContext}.{capa}.{subcapa}
```

Ejemplo: `com.taxmanager.taxmanagerapi.taxpayer.application.service`

### Naming de Clases

| Componente                  | Patrón                          | Ejemplo                          |
|-----------------------------|---------------------------------|----------------------------------|
| Entidad de dominio          | `{Entity}`                      | `Taxpayer`                       |
| Repositorio (puerto)        | `{Entity}Repository`            | `TaxpayerRepository`             |
| Use Case (command)          | `{Action}{Entity}UseCase`       | `CreateTaxpayerUseCase`          |
| Use Case (query)            | `Get{Entity}UseCase`            | `GetTaxpayerUseCase`             |
| Service                     | `{Action}{Entity}Service`       | `CreateTaxpayerService`          |
| Spring Data Repo            | `SpringData{Entity}Repository`  | `SpringDataTaxpayerRepository`   |
| Adapter                     | `{Entity}RepositoryAdapter`     | `TaxpayerRepositoryAdapter`      |
| Controller                  | `{Entity}Controller`            | `TaxpayerController`             |
| Command (app DTO)           | `{Action}{Entity}Command`       | `CreateTaxpayerCommand`          |
| Result (app DTO)            | `{Entity}ItemResult` / `{Entity}DetailResult` | `TaxpayerDetailResult` |
| Request (web DTO)           | `{Action}{Entity}Request`       | `CreateTaxpayerRequest`          |
| Response (web DTO)          | `{Action}{Entity}Response`      | `CreateTaxpayerResponse`         |
| Web Mapper                  | `{Entity}WebMapper`             | `TaxpayerWebMapper`              |

Referencia completa en [docs/DOMAIN_GLOSSARY.md](docs/DOMAIN_GLOSSARY.md).

---

## Estilo de Código

### Java

- **Java 21 features:** Usar records, pattern matching, text blocks donde aplique.
- **Records para todos los DTOs:** Command, Result, Request, Response.
- **Lombok solo en entidades JPA:** `@Getter`, `@NoArgsConstructor(access = PROTECTED)`.
  No usar `@Data`, `@Setter`, `@Builder` ni `@AllArgsConstructor`.
- **Imports:** No usar wildcards (`import java.util.*`). Importar clases específicas.
- **Var:** Usar `var` solo cuando el tipo es obvio por el lado derecho de la asignación.
- **Optional:** Usar `Optional` como retorno de métodos, nunca como parámetro ni campo.
- **Null:** Evitar `null`. Usar `Optional` para retornos, y validar con `Objects.requireNonNull()` en constructores.

### Entidades de Dominio (Always Valid Domain)

```java
// ✅ CORRECTO
Taxpayer taxpayer = Taxpayer.create("López S.R.L.", "20123456783", ...);
taxpayer.updateInfo("Nuevo nombre", ...);
taxpayer.deactivate();

// ❌ INCORRECTO — nunca crear con new ni usar setters
Taxpayer t = new Taxpayer();
t.setBusinessName("...");
```

### DTOs (Records)

```java
// Application layer — sin anotaciones web
public record CreateTaxpayerCommand(
    String businessName,
    String cuit,
    TaxCondition taxCondition,
    String fiscalAddress,
    String email,
    String phone
) {}

// Web layer — con Bean Validation + Swagger
public record CreateTaxpayerRequest(
    @NotBlank @Schema(description = "Razón social", example = "López S.R.L.")
    String businessName,

    @NotBlank @Schema(description = "CUIT con guiones", example = "30-71234567-9")
    String cuit,

    // ...
) {}
```

### Controllers

- Un controller por entidad.
- Inyectar Use Cases (interfaces), no Services directamente.
- Usar `@Valid` en los request bodies.
- Devolver `ResponseEntity` con el código HTTP correcto.

### Services

- Un service por use case (command) o por grupo de queries.
- Anotados con `@Service`.
- Implementan la interfaz del Use Case.
- Inyectan el repositorio de dominio (interfaz), no el Spring Data.

---

## Base de Datos

### Migraciones Flyway

- Directorio: `src/main/resources/db/migration/`
- Convención: `V{número}__{descripcion_snake_case}.sql`
- Ejemplo: `V1__create_users_table.sql`
- **Inmutables:** Una vez aplicada, nunca se modifica. Los cambios van en migraciones nuevas.

### Naming SQL

| Elemento      | Convención             | Ejemplo                    |
|---------------|------------------------|----------------------------|
| Tablas        | `snake_case`, plural   | `taxpayers`, `vouchers`    |
| Columnas      | `snake_case`           | `business_name`, `cuit`    |
| FK            | `{tabla_singular}_id`  | `taxpayer_id`              |
| Índices       | `idx_{tabla}_{cols}`   | `idx_taxpayers_cuit`       |
| Unique        | `uk_{tabla}_{cols}`    | `uk_taxpayers_cuit`        |
| PK            | `pk_{tabla}`           | `pk_taxpayers`             |
| FK constraint | `fk_{tabla}_{col}`     | `fk_vouchers_taxpayer_id`  |

---

## Testing

### Convenciones de tests

- Directorio espejo: `src/test/java/` con la misma estructura de paquetes.
- Nombre de clase: `{ClaseTesteada}Test.java` (unit) o `{ClaseTesteada}IT.java` (integración).
- Método de test: `should_resultado_when_condicion()` o `shouldResultadoWhenCondicion()`.

### Niveles de test

| Nivel       | Foco                         | Anotación / Herramienta           |
|-------------|------------------------------|-----------------------------------|
| Unit        | Entidades, services          | JUnit 5 + Mockito                 |
| Integration | Controllers, repositories    | `@WebMvcTest`, `@DataJpaTest`     |
| E2E         | Flujo completo               | Testcontainers + RestAssured      |

### Prioridad

1. **Entidades de dominio** — unit tests exhaustivos para `create()`, métodos de
   comportamiento, y validaciones. Son tests rápidos y de alto valor.
2. **Services** — unit tests con mocks.
3. **Controllers** — integration tests con `@WebMvcTest`.
4. **Flujos E2E** — tests completos HTTP→BD→HTTP con Testcontainers (pocos, selectivos).

---

## Responses HTTP

| Operación     | Código              | Body                        |
|---------------|---------------------|-----------------------------|
| Crear recurso | `201 Created`       | El recurso creado           |
| Obtener       | `200 OK`            | El recurso o lista paginada |
| Actualizar    | `200 OK`            | El recurso actualizado      |
| Eliminar      | `204 No Content`    | Sin body                    |
| Error formato | `400 Bad Request`   | ProblemDetail (RFC 7807)    |
| No autenticado| `401 Unauthorized`  | ProblemDetail               |
| Sin permisos  | `403 Forbidden`     | ProblemDetail               |
| No encontrado | `404 Not Found`     | ProblemDetail               |
| Conflicto     | `409 Conflict`      | ProblemDetail               |
| Error negocio | `422 Unprocessable` | ProblemDetail               |

---

## Documentación

- **CHANGELOG.md:** Actualizar con cada cambio notable (seguir Keep a Changelog).
- **docs/:** No crear documentos nuevos sin necesidad. Mantener los existentes actualizados.
- **ADRs:** Crear uno nuevo cuando se tome una decisión arquitectónica significativa.
  Nunca eliminar un ADR — si se revierte, se crea uno nuevo con estado "Reemplaza ADR-XXX".
- **Swagger:** Las anotaciones `@Schema` en los Web DTOs deben tener `description`
  y `example` para cada campo.
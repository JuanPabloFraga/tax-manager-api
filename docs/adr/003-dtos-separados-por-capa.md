# ADR-003: DTOs Separados por Capa (Web vs Application)

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

En una arquitectura hexagonal, los DTOs de la API REST (con anotaciones de Bean
Validation y Swagger) no deberían contaminar la capa de aplicación. Sin embargo,
duplicar DTOs tiene un costo en mantenimiento.

Necesitamos decidir si usamos un solo set de DTOs para todo, o dos sets con un
mapper entre ellos.

## Decisión

Usamos **dos sets de DTOs** con un **WebMapper manual** (`@Component`) que traduce
entre ellos:

| Capa        | DTOs                    | Anotaciones               |
|-------------|-------------------------|---------------------------|
| Web         | `*Request`, `*Response` | `@NotBlank`, `@Schema`, etc. |
| Application | `*Command`, `*Result`   | Ninguna (records puros)   |

El `WebMapper` es un `@Component` escrito a mano (no MapStruct):

```java
@Component
public class TaxpayerWebMapper {
    public CreateTaxpayerCommand toCommand(CreateTaxpayerRequest request) { ... }
    public CreateTaxpayerResponse toResponse(TaxpayerDetailResult result) { ... }
}
```

Todos los DTOs son **Java Records** (inmutables, concisos, sin Lombok).

## Alternativas Consideradas

### A) Un solo set de DTOs (compartidos)
```java
// El mismo record se usa en controller y service
public record CreateTaxpayerRequest(@NotBlank String businessName, ...)
```
- **Pro:** Menos código, menos mapeos.
- **Contra:** La capa de aplicación depende de Bean Validation y Swagger (annotations
  web). Si cambias el contrato de la API, afectas la lógica de aplicación.

### B) Dos sets con MapStruct
- **Pro:** MapStruct genera el código de mapeo automáticamente.
- **Contra:** Los WebMappers son pocos y tienen lógica específica (formatear CUIT
  con guiones, etc.). MapStruct agrega complejidad de configuración para algo simple.

### C) Dos sets con WebMapper manual (elegida)
- **Pro:** Separación limpia. La capa de aplicación no sabe de HTTP. Los mappers
  son explícitos y fáciles de debuggear.
- **Contra:** Código de mapeo manual (boilerplate). Aceptable dado el tamaño del
  proyecto (4 bounded contexts, ~5 mappers).

## Consecuencias

- **Positivas:**
  - La capa de aplicación es independiente del framework web.
  - Se puede cambiar de REST a GraphQL tocando solo `infrastructure/web/`.
  - Los Command/Result records son super limpios y testeables.
  - El CUIT se puede formatear/normalizar en el WebMapper sin contaminar el dominio.

- **Negativas:**
  - Más clases (Request + Response + Command + Result por operación).
  - Mapeo manual puede ser tedioso con muchos campos. Aceptable en un MVP.

- **Nota sobre MapStruct:**
  - MapStruct **sí se usa** si hay mapeo repetitivo entity↔DTO en el RepositoryAdapter.
  - La decisión de no usarlo aplica solo a los WebMappers (web ↔ application).

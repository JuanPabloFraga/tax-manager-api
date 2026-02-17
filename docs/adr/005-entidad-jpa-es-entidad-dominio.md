# ADR-005: Entidad JPA = Entidad de Dominio (Modelo Compartido)

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

En una arquitectura hexagonal pura, la entidad de dominio y la entidad JPA son
clases separadas. La entidad de dominio es Java puro (sin anotaciones de framework),
y la entidad JPA es un "modelo de persistencia" que se mapea desde/hacia el dominio
en el adapter.

Esto implica duplicar clases y agregar mapeos en el RepositoryAdapter. Necesitamos
decidir si esa separación vale la pena en un MVP.

## Decisión

Usamos una **única clase** que es tanto la entidad de dominio como la entidad JPA.
Es decir, la misma clase tiene:

- Anotaciones JPA (`@Entity`, `@Table`, `@Column`, etc.)
- Anotaciones Lombok (`@Getter`, `@NoArgsConstructor(access = PROTECTED)`)
- Lógica de dominio (factory method `create()`, métodos de comportamiento, validaciones)

```java
@Entity
@Table(name = "taxpayers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Taxpayer {

    @Id
    private UUID id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    // ... más campos

    public static Taxpayer create(String businessName, String cuit, ...) {
        // Validaciones de dominio
        // ...
        return new Taxpayer(businessName, cuit, ...);
    }

    private Taxpayer(String businessName, String cuit, ...) {
        this.id = UUID.randomUUID();
        this.businessName = businessName;
        // ...
    }
}
```

## Alternativas Consideradas

### A) Modelo separado (dominio ≠ JPA)
```
domain/entity/Taxpayer.java        → Java puro, sin @Entity
persistence/entity/TaxpayerJpa.java → @Entity, @Table, solo datos
persistence/mapper/TaxpayerMapper.java → MapStruct dominio ↔ JPA
```
- **Pro:** Pureza hexagonal. El dominio no depende de JPA.
- **Contra:** Duplica cada entidad (x2 clases). Agrega un mapper por entidad.
  Para un MVP con 4 entidades simples, es mucho overhead con poco beneficio.

### B) Modelo compartido (elegida)
```
domain/entity/Taxpayer.java → @Entity + lógica de dominio
```
- **Pro:** Sin duplicación. Menos clases, menos mapeos. El RepositoryAdapter
  trabaja directamente con la entidad de dominio.
- **Contra:** La entidad de dominio tiene anotaciones JPA (dependencia de framework
  en la capa de dominio). `@NoArgsConstructor(PROTECTED)` existe solo por Hibernate.

## Consecuencias

- **Positivas:**
  - Menos código (~50% menos clases de entidad y mappers).
  - El RepositoryAdapter es más simple (no mapea entre dos modelos).
  - Más fácil de mantener y de entender para un equipo chico.

- **Negativas:**
  - La capa de dominio tiene una dependencia en `jakarta.persistence` y Lombok.
  - Si el modelo de dominio diverge del modelo de persistencia (ej: se agrega
    un campo calculado que no va a BD, o se normaliza una tabla), hay que separar.
  - `@NoArgsConstructor(access = PROTECTED)` es un "leak" de infraestructura en
    el dominio (Hibernate lo necesita para reflexión).

- **Criterio de migración:**
  - Si en el futuro una entidad necesita relaciones complejas, herencia de tabla,
    o el model de dominio diverge significativamente del esquema de BD, se separan
    las clases para ese bounded context específico. No es necesario migrar todo a
    la vez.

- **Mitigación:**
  - El constructor protegido de Hibernate nunca se usa en el código de negocio
    (no es público). El factory method `create()` sigue siendo la única forma
    válida de crear la entidad.
  - Las anotaciones JPA son "ruido visual" pero no afectan la lógica de negocio.

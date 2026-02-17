# ADR-002: Always Valid Domain con Factory Methods

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

Las entidades de dominio (Taxpayer, Voucher, User) tienen reglas de negocio estrictas:
un CUIT debe tener 11 dígitos y pasar validación módulo 11, los montos de un comprobante
deben cuadrar (`net + vat + exempt = total`), etc.

Necesitamos decidir dónde y cómo validar estas reglas para garantizar que una entidad
nunca exista en un estado inválido.

## Decisión

Adoptamos el patrón **Always Valid Domain**:

1. **Constructor privado** — no se puede instanciar una entidad con `new`.
2. **Factory method estático `create()`** — único punto de creación, ejecuta todas
   las validaciones de negocio antes de devolver la instancia.
3. **Sin setters públicos** — la mutación solo ocurre vía métodos de comportamiento
   con nombre expresivo (ej: `updateInfo()`, `deactivate()`).
4. **Cada método de mutación re-valida** las invariantes de negocio afectadas.
5. **Lanza `DomainValidationException`** (HTTP 422) si alguna regla se rompe.

Lombok se usa exclusivamente para lo que necesita JPA:
- `@Getter` — para acceder a los campos.
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` — para que Hibernate pueda
  instanciar la entidad vía reflexión.

## Alternativas Consideradas

### A) Validación en el Service
```java
// Service valida antes de crear
if (cuit == null || cuit.length() != 11) throw ...;
Taxpayer t = new Taxpayer();
t.setCuit(cuit);
```
- **Pro:** Simple, el service tiene todo el control.
- **Contra:** La entidad puede existir en estado inválido. Si se crea desde otro
  lugar (test, migración), no hay validación. Lógica de negocio dispersa.

### B) Bean Validation en la entidad (@Valid)
```java
@Entity
public class Taxpayer {
    @NotBlank @Size(min=11, max=11)
    private String cuit;
}
```
- **Pro:** Declarativo, familiar.
- **Contra:** Las reglas complejas (módulo 11, `net + vat + exempt = total`) no se
  expresan bien con anotaciones. Mezcla concerns de validación web con dominio.

### C) Always Valid Domain con factory methods (elegida)
```java
public static Taxpayer create(String businessName, String cuit, ...) {
    // Todas las validaciones aquí
    return new Taxpayer(businessName, cuit, ...);
}
```
- **Pro:** La entidad siempre es válida. Las reglas están junto a los datos.
  Testeable sin framework.
- **Contra:** Más código que un POJO con setters. Requiere disciplina.

## Consecuencias

- **Positivas:**
  - Imposible tener una entidad en estado inválido en memoria.
  - Las reglas de negocio son testeables unitariamente sin Spring ni BD.
  - DomainValidationException fluye naturalmente al GlobalExceptionHandler → 422.
  - El código expresa el negocio ("comprobante se crea", no "se setean campos").

- **Negativas:**
  - JPA necesita un constructor sin argumentos para reflexión → se agrega con
    `@NoArgsConstructor(access = PROTECTED)` y nunca se usa en el código de negocio.
  - Los tests de dominio necesitan usar `create()` en lugar de builders o constructores.

- **Riesgos:**
  - Si se agrega un campo nuevo a la entidad, hay que recordar agregarlo al factory
    method y validarlo. Mitigación: tests unitarios para cada campo.

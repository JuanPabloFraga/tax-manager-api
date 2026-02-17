# ADR-006: UUID como Identificador de Entidades

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

Cada entidad necesita un identificador único (Primary Key). Las opciones más comunes
son autoincremental (`SERIAL`/`BIGSERIAL` en PostgreSQL), UUID, o un identificador
de negocio (como el CUIT para contribuyentes).

La elección afecta la seguridad, el rendimiento, la generación de datos en tests,
y las URLs de la API.

## Decisión

Usamos **UUID v4 (random)** como Primary Key en todas las tablas.

- **En PostgreSQL:** Tipo nativo `UUID` con default `gen_random_uuid()`.
- **En Java:** `java.util.UUID`, generado con `UUID.randomUUID()` en el factory
  method `create()` de cada entidad.
- **En la API:** Se expone como String en formato estándar (`a1b2c3d4-e5f6-7890-...`).

```sql
CREATE TABLE taxpayers (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    -- ...
);
```

```java
private Taxpayer(...) {
    this.id = UUID.randomUUID();
    // ...
}
```

## Alternativas Consideradas

### A) Autoincremental (SERIAL/BIGSERIAL)
```
/api/v1/taxpayers/1
/api/v1/taxpayers/2
/api/v1/taxpayers/3
```
- **Pro:** Simple, eficiente en índices B-tree, menor tamaño en disco (8 bytes vs 16).
- **Contra:** Expone información: un competidor puede inferir cuántos registros hay
  (`/taxpayers/5000` → "tienen 5000 contribuyentes"). Vulnerable a enumeración
  (`/taxpayers/1`, `/taxpayers/2`, ...). Conflictos en merges de datos.

### B) UUID v4 random (elegida)
```
/api/v1/taxpayers/b2c3d4e5-f6a7-8901-bcde-f23456789012
```
- **Pro:** No enumerable, no expone cardinalidad, generación sin coordinación
  (no necesita la BD para generar el ID). Estándar en APIs REST modernas.
- **Contra:** 16 bytes vs 8 (autoincremental). Índices B-tree menos eficientes
  por la distribución aleatoria. Mitigado: el volumen de datos de un contador
  independiente no justifica esa preocupación.

### C) UUID v7 (time-ordered)
- **Pro:** Ordenados por tiempo, mejor rendimiento en índices B-tree que UUID v4.
- **Contra:** Requiere Java 21+ con librería externa o implementación custom.
  La ganancia de rendimiento es irrelevante para el volumen de datos del MVP.

### D) Identificador de negocio como PK (CUIT)
- **Pro:** Natural para contribuyentes, no se necesita PK artificial.
- **Contra:** No todas las entidades tienen un identificador de negocio estable.
  El CUIT podría cambiar (aunque es raro). Los comprobantes no tienen un ID
  natural obvio. Inconsistente entre entidades.

## Consecuencias

- **Positivas:**
  - URLs no enumerables → mejor seguridad.
  - Los IDs se pueden generar en la aplicación antes de persistir (útil para
    eventos, logs, y testing).
  - Consistencia: todas las entidades usan el mismo tipo de PK.
  - PostgreSQL tiene tipo `UUID` nativo con buen soporte.

- **Negativas:**
  - Los logs y debugging son menos legibles (`b2c3d4e5-f6a7-...` vs `42`).
  - Ligeramente más espacio en disco y en índices (irrelevante para el volumen).
  - Las URLs son más largas.

- **Nota sobre rendimiento:**
  - Para el caso de uso de un contador independiente con cientos/miles de registros,
    la diferencia de rendimiento entre UUID v4 y SERIAL es imperceptible.
  - Si el proyecto escala a millones de registros, se puede evaluar migrar a UUID v7
    o agregar índices optimizados.

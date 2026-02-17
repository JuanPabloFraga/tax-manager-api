# ADR-001: Arquitectura Hexagonal por Paquetes

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

Necesitamos definir cómo organizar el código del proyecto. Las opciones van desde
una estructura clásica por capas técnicas (`controller/`, `service/`, `repository/`)
hasta una arquitectura hexagonal completa con módulos Maven separados por bounded context.

El proyecto es un MVP desarrollado por un equipo pequeño (una persona inicialmente).
La base de código será mediana (~50-100 clases). Necesitamos buena separación de
responsabilidades sin sobreingeniería.

## Decisión

Adoptamos una **arquitectura hexagonal (Ports & Adapters) organizada por bounded
contexts**, donde la separación se implementa **por paquetes Java**, no por módulos
Maven.

Cada bounded context (`auth/`, `taxpayer/`, `voucher/`, `vatbook/`) es un paquete
raíz que contiene internamente las capas `domain/`, `application/` e `infrastructure/`.

## Alternativas Consideradas

### A) Estructura por capas técnicas
```
controller/ → service/ → repository/
```
- **Pro:** Simple, familiar.
- **Contra:** Mezcla conceptos de negocio. Un cambio en "contribuyentes" toca
  archivos en 3+ paquetes distintos. No escala.

### B) Hexagonal con módulos Maven separados
```
taxpayer-domain/ (módulo Maven) → taxpayer-application/ → taxpayer-infrastructure/
```
- **Pro:** Fuerza la separación en tiempo de compilación.
- **Contra:** Excesivo para un MVP. Complica el build, el IDE, y el onboarding.

### C) Hexagonal por paquetes (elegida)
```
taxpayer/domain/ → taxpayer/application/ → taxpayer/infrastructure/
```
- **Pro:** Buena separación, fácil de navegar, sin overhead de build.
- **Contra:** La regla de dependencia entre capas se debe respetar por convención
  (no la fuerza el compilador). Se mitiga con code reviews y ArchUnit tests.

## Consecuencias

- **Positivas:**
  - Cada cambio de negocio está localizado en un solo bounded context.
  - La arquitectura es clara para escalar a módulos Maven si algún día se necesita.
  - Bajo costo de setup y mantenimiento.

- **Negativas:**
  - Las dependencias entre capas se respetan por disciplina, no por compilador.
  - Se recomienda agregar tests de arquitectura (ArchUnit) para automatizar la verificación.

- **Riesgos:**
  - Un desarrollador nuevo podría importar clases de `infrastructure` desde `domain`
    sin darse cuenta. Mitigación: documentación + ArchUnit.

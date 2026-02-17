# Visión del Producto — Tax Manager API

## Problema

Los contadores independientes en Argentina gestionan la información fiscal de sus clientes
(contribuyentes) de forma manual: planillas de Excel, papeles, o sistemas genéricos que no
reflejan la realidad fiscal argentina. Esto genera:

- **Errores en la carga de comprobantes** (montos que no cuadran, CUIT inválidos)
- **Pérdida de tiempo** generando libros IVA manualmente
- **Riesgo fiscal** por inconsistencias entre comprobantes y declaraciones
- **Dificultad para escalar** cuando crece la cantidad de clientes

## Solución

**Tax Manager API** es una API REST que permite a un contador independiente:

1. **Administrar sus clientes (contribuyentes):** alta, edición, consulta y búsqueda
   por CUIT, con validaciones fiscales argentinas integradas.
2. **Registrar comprobantes de compra y venta:** facturas, notas de crédito y débito,
   con validación automática de coherencia de montos.
3. **Generar libros IVA:** compras y ventas agrupados por período fiscal, con cálculo
   automático de totales (neto gravado, IVA, exento).

## Usuario objetivo

**Contador independiente** que gestiona la situación fiscal de múltiples clientes
(contribuyentes). Necesita una herramienta confiable que valide datos fiscales y le
ahorre tiempo en tareas repetitivas.

## Qué NO es este proyecto

- **No es un sistema de facturación electrónica.** No emite comprobantes ante AFIP
  (esto se contempla como funcionalidad futura).
- **No es un ERP.** No gestiona stock, cobros, pagos ni contabilidad general.
- **No es multi-tenant.** Está diseñado para un único contador/estudio. Cada instancia
  es independiente.
- **No reemplaza a un contador.** Es una herramienta de apoyo, no toma decisiones
  fiscales.

## Funcionalidades del MVP (v1.0)

### F1 — Gestión de Contribuyentes
| Aspecto           | Detalle                                                    |
|-------------------|------------------------------------------------------------|
| Descripción       | CRUD completo de contribuyentes                            |
| Campos requeridos | Razón social/nombre, CUIT, condición fiscal, domicilio     |
| Validaciones      | CUIT válido (algoritmo módulo 11), condición fiscal válida |
| Búsqueda          | Por CUIT, por razón social (parcial)                       |

### F2 — Registro de Comprobantes
| Aspecto           | Detalle                                                              |
|-------------------|----------------------------------------------------------------------|
| Descripción       | Registro de comprobantes de compra y venta                           |
| Tipos soportados  | Factura A/B/C, Nota de Crédito A/B/C, Nota de Débito A/B/C          |
| Campos requeridos | Fecha, tipo, punto de venta, número, neto gravado, IVA, exento, total|
| Validaciones      | Montos positivos, neto + IVA + exento = total                        |
| Relación          | Cada comprobante pertenece a un contribuyente                        |

### F3 — Generación de Libros IVA
| Aspecto           | Detalle                                                    |
|-------------------|------------------------------------------------------------|
| Descripción       | Generación de libros IVA compras y ventas por período      |
| Agrupación        | Por tipo (compras/ventas) y período (mes/año)              |
| Ordenamiento      | Cronológico por fecha de comprobante                       |
| Totales           | Neto gravado, IVA, exento, total del período               |

### F4 — Autenticación y Autorización
| Aspecto           | Detalle                                                    |
|-------------------|------------------------------------------------------------|
| Descripción       | Sistema de autenticación stateless con JWT                 |
| Funcionalidades   | Registro, login, refresh token, logout                     |
| Roles             | ADMIN, ACCOUNTANT, VIEWER                                  |
| Protección        | Todos los endpoints requieren autenticación excepto login  |

## Roadmap — Funcionalidades Futuras

Estas funcionalidades **no forman parte del MVP** pero la arquitectura está diseñada
para soportarlas:

### Fase 2 — Reportes
- Exportación de libros IVA a **PDF** y **Excel**
- Reportes personalizados por contribuyente
- Resumen fiscal por período

### Fase 3 — Integración AFIP
- Consulta de datos de contribuyente vía **AFIP Web Services**
- Emisión de comprobantes electrónicos (WSFE)
- Validación de comprobantes recibidos contra AFIP

### Fase 4 — Dashboard y Métricas
- Dashboard con indicadores fiscales clave
- Gráficos de evolución de IVA compras/ventas
- Alertas de vencimientos fiscales

## Restricciones y Supuestos

### Restricciones
- El sistema opera exclusivamente con normativa fiscal **argentina**
- La moneda es **ARS (Peso Argentino)** — no se contempla multi-moneda en el MVP
- La alícuota de IVA se maneja como dato del comprobante, no se calcula automáticamente

### Supuestos
- El usuario tiene conocimiento fiscal básico (sabe qué es un CUIT, una Factura A, etc.)
- Los comprobantes se cargan manualmente (no hay integración automática en el MVP)
- Se asume una única alícuota de IVA por comprobante (simplificación del MVP)

## Stack Tecnológico

| Componente     | Tecnología                          |
|----------------|-------------------------------------|
| Lenguaje       | Java 21 LTS                        |
| Framework      | Spring Boot 4.0.x                   |
| Build          | Maven                               |
| Base de datos  | PostgreSQL 16                       |
| ORM            | Spring Data JPA (Hibernate)         |
| Migraciones    | Flyway                              |
| Autenticación  | JWT (jjwt)                          |
| Documentación  | SpringDoc OpenAPI (Swagger)         |
| Mapping        | MapStruct                           |
| Testing        | JUnit 5 + Mockito + Testcontainers  |
| CI/CD          | GitHub Actions                      |
| Contenedores   | Docker + Docker Compose             |

## Arquitectura

El proyecto sigue una **arquitectura hexagonal (Ports & Adapters)** pragmática,
organizada por bounded contexts. Cada módulo de dominio es independiente y se comunica
a través de interfaces (puertos).

Ver [ARCHITECTURE.md](./ARCHITECTURE.md) para detalles completos.
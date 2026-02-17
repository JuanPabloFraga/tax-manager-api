# Glosario de Dominio — Tax Manager API

Este documento mapea cada concepto del dominio fiscal argentino al nombre utilizado
en el código. Es la referencia canónica para mantener consistencia en todo el proyecto.

> **Convención:** El código está en inglés. La documentación y los mensajes de error
> orientados al usuario están en español.

---

## Bounded Contexts

| Concepto de negocio         | Bounded Context (paquete) | Notas                                      |
|-----------------------------|---------------------------|---------------------------------------------|
| Autenticación y autorización | `auth`                   | Registro, login, JWT, roles                 |
| Contribuyentes (clientes)   | `taxpayer`               | CRUD de clientes del contador               |
| Comprobantes fiscales        | `voucher`                | Facturas, notas de crédito/débito           |
| Libros IVA                   | `vatbook`                | Query-only, sin entidad propia              |

---

## Entidades y Agregados

| Término fiscal (ES)   | Nombre en código (EN) | Clase Java          | Tabla PostgreSQL | Notas                                                    |
|------------------------|-----------------------|---------------------|------------------|----------------------------------------------------------|
| Contribuyente / Cliente | Taxpayer             | `Taxpayer.java`     | `taxpayers`      | Persona física o jurídica inscripta ante AFIP             |
| Comprobante            | Voucher               | `Voucher.java`      | `vouchers`       | Término genérico para factura, NC, ND                     |
| Usuario del sistema    | User                  | `User.java`         | `users`          | Contador que opera el sistema                             |
| Refresh Token          | RefreshToken          | `RefreshToken.java` | `refresh_tokens` | Token de refresco para renovar access tokens              |

---

## Enumeraciones

### Condición Fiscal — `TaxCondition`

| Valor AFIP (ES)                       | Enum value                  | Notas                                    |
|----------------------------------------|-----------------------------|------------------------------------------|
| Responsable Inscripto                  | `RESPONSABLE_INSCRIPTO`     | Inscripto en IVA, emite Factura A        |
| Monotributista                         | `MONOTRIBUTISTA`            | Régimen simplificado, emite Factura C    |
| Exento                                 | `EXENTO`                    | Exento de IVA                            |
| Consumidor Final                       | `CONSUMIDOR_FINAL`          | No inscripto, recibe Factura B           |
| No Responsable                         | `NO_RESPONSABLE`            | No alcanzado por IVA                     |

> Los valores del enum usan los nombres fiscales argentinos en UPPER_SNAKE_CASE
> porque son términos técnicos con significado legal específico.

### Tipo de Comprobante — `VoucherType`

| Tipo fiscal (ES)     | Enum value       | Notas                                        |
|----------------------|------------------|----------------------------------------------|
| Factura A            | `FACTURA_A`      | Entre Responsables Inscriptos                 |
| Factura B            | `FACTURA_B`      | RI → Consumidor Final / Exento / Monotributo  |
| Factura C            | `FACTURA_C`      | Emitida por Monotributistas                   |
| Nota de Crédito A    | `NOTA_CREDITO_A` | Anula o ajusta una Factura A                  |
| Nota de Crédito B    | `NOTA_CREDITO_B` | Anula o ajusta una Factura B                  |
| Nota de Crédito C    | `NOTA_CREDITO_C` | Anula o ajusta una Factura C                  |
| Nota de Débito A     | `NOTA_DEBITO_A`  | Ajuste positivo sobre Factura A               |
| Nota de Débito B     | `NOTA_DEBITO_B`  | Ajuste positivo sobre Factura B               |
| Nota de Débito C     | `NOTA_DEBITO_C`  | Ajuste positivo sobre Factura C               |

### Categoría de Comprobante — `VoucherCategory`

| Concepto (ES)    | Enum value | Notas                                          |
|------------------|------------|-------------------------------------------------|
| Compra           | `PURCHASE` | Comprobante recibido de un proveedor            |
| Venta            | `SALE`     | Comprobante emitido a un cliente                |

### Rol de Usuario — `Role`

| Rol (ES)        | Enum value   | Permisos                                       |
|-----------------|--------------|-------------------------------------------------|
| Administrador   | `ADMIN`      | Acceso total, gestión de usuarios               |
| Contador        | `ACCOUNTANT` | CRUD contribuyentes, comprobantes, libros IVA   |
| Visualizador    | `VIEWER`     | Solo lectura                                    |

---

## Campos y Atributos

### Contribuyente (`Taxpayer`)

| Campo fiscal (ES)   | Atributo Java    | Columna DB          | Tipo Java       | Tipo DB            | Notas                                                      |
|----------------------|------------------|----------------------|-----------------|--------------------|-------------------------------------------------------------|
| ID                   | `id`             | `id`                 | `UUID`          | `UUID`             | PK, generado automáticamente                                |
| Razón Social         | `businessName`   | `business_name`      | `String`        | `VARCHAR(200)`     | Nombre legal o nombre de fantasía                           |
| CUIT                 | `cuit`           | `cuit`               | `String`        | `VARCHAR(11)`      | Sin guiones en BD (`20123456783`), con guiones en API       |
| Condición Fiscal     | `taxCondition`   | `tax_condition`      | `TaxCondition`  | `VARCHAR(30)`      | Enum almacenado como String                                 |
| Domicilio Fiscal     | `fiscalAddress`  | `fiscal_address`     | `String`        | `VARCHAR(300)`     | Dirección fiscal completa (campo libre)                     |
| Email                | `email`          | `email`              | `String`        | `VARCHAR(150)`     | Opcional, contacto del contribuyente                        |
| Teléfono             | `phone`          | `phone`              | `String`        | `VARCHAR(30)`      | Opcional                                                    |
| Activo               | `active`         | `active`             | `boolean`       | `BOOLEAN`          | Soft delete, default `true`                                 |
| Fecha creación       | `createdAt`      | `created_at`         | `LocalDateTime` | `TIMESTAMP`        | Auditoría automática                                        |
| Fecha modificación   | `updatedAt`      | `updated_at`         | `LocalDateTime` | `TIMESTAMP`        | Auditoría automática                                        |

### Comprobante (`Voucher`)

| Campo fiscal (ES)    | Atributo Java     | Columna DB           | Tipo Java         | Tipo DB          | Notas                                                      |
|-----------------------|-------------------|----------------------|-------------------|------------------|-------------------------------------------------------------|
| ID                    | `id`              | `id`                 | `UUID`            | `UUID`           | PK                                                          |
| Contribuyente        | `taxpayer`        | `taxpayer_id`        | `Taxpayer`        | `UUID` (FK)      | Relación ManyToOne                                          |
| Categoría            | `category`        | `category`           | `VoucherCategory` | `VARCHAR(10)`    | PURCHASE o SALE                                             |
| Tipo de comprobante  | `voucherType`     | `voucher_type`       | `VoucherType`     | `VARCHAR(20)`    | FACTURA_A, NOTA_CREDITO_B, etc.                             |
| Fecha de emisión     | `issueDate`       | `issue_date`         | `LocalDate`       | `DATE`           | Fecha del comprobante                                       |
| Punto de venta       | `pointOfSale`     | `point_of_sale`      | `int`             | `INTEGER`        | Número de punto de venta (1-99999)                          |
| Número               | `voucherNumber`   | `voucher_number`     | `long`            | `BIGINT`         | Número secuencial del comprobante                           |
| Neto Gravado         | `netAmount`       | `net_amount`         | `BigDecimal`      | `NUMERIC(19,4)`  | Monto sujeto a IVA                                         |
| IVA                  | `vatAmount`       | `vat_amount`         | `BigDecimal`      | `NUMERIC(19,4)`  | Monto del impuesto al valor agregado                        |
| Exento               | `exemptAmount`    | `exempt_amount`      | `BigDecimal`      | `NUMERIC(19,4)`  | Monto exento de IVA                                        |
| Total                | `totalAmount`     | `total_amount`       | `BigDecimal`      | `NUMERIC(19,4)`  | netAmount + vatAmount + exemptAmount                        |
| Descripción          | `description`     | `description`        | `String`          | `VARCHAR(500)`   | Opcional, nota del comprobante                              |
| Fecha creación       | `createdAt`       | `created_at`         | `LocalDateTime`   | `TIMESTAMP`      | Auditoría                                                   |
| Fecha modificación   | `updatedAt`       | `updated_at`         | `LocalDateTime`   | `TIMESTAMP`      | Auditoría                                                   |

### Usuario (`User`)

| Campo (ES)           | Atributo Java    | Columna DB          | Tipo Java        | Tipo DB          | Notas                                                      |
|----------------------|------------------|----------------------|------------------|------------------|-------------------------------------------------------------|
| ID                   | `id`             | `id`                 | `UUID`           | `UUID`           | PK                                                          |
| Email                | `email`          | `email`              | `String`         | `VARCHAR(150)`   | Único, usado como username                                  |
| Contraseña           | `password`       | `password`           | `String`         | `VARCHAR(255)`   | Hash BCrypt                                                 |
| Nombre completo      | `fullName`       | `full_name`          | `String`         | `VARCHAR(150)`   | Nombre para mostrar                                         |
| Rol                  | `role`           | `role`               | `Role`           | `VARCHAR(20)`    | ADMIN, ACCOUNTANT, VIEWER                                   |
| Activo               | `active`         | `active`             | `boolean`        | `BOOLEAN`        | Soft delete                                                 |
| Fecha creación       | `createdAt`      | `created_at`         | `LocalDateTime`  | `TIMESTAMP`      | Auditoría                                                   |

### Refresh Token (`RefreshToken`)

| Campo (ES)           | Atributo Java    | Columna DB          | Tipo Java        | Tipo DB          | Notas                                                      |
|----------------------|------------------|----------------------|------------------|------------------|-------------------------------------------------------------|
| ID                   | `id`             | `id`                 | `UUID`           | `UUID`           | PK                                                          |
| Token                | `token`          | `token`              | `String`         | `VARCHAR(500)`   | Valor del refresh token                                     |
| Usuario              | `user`           | `user_id`            | `User`           | `UUID` (FK)      | Relación ManyToOne                                          |
| Fecha expiración     | `expiresAt`      | `expires_at`         | `LocalDateTime`  | `TIMESTAMP`      | Cuándo expira el token                                      |
| Revocado             | `revoked`        | `revoked`            | `boolean`        | `BOOLEAN`        | Para logout / invalidación                                  |
| Fecha creación       | `createdAt`      | `created_at`         | `LocalDateTime`  | `TIMESTAMP`      | Auditoría                                                   |

---

## Conceptos Fiscales Clave

| Término fiscal (ES)         | Traducción en código (EN) | Definición                                                                                               |
|------------------------------|---------------------------|----------------------------------------------------------------------------------------------------------|
| CUIT                         | `cuit`                    | Clave Única de Identificación Tributaria. 11 dígitos, validados con algoritmo módulo 11.                 |
| IVA                          | `vat`                     | Impuesto al Valor Agregado (Value Added Tax). Alícuota general: 21%.                                    |
| Libro IVA Compras            | `purchaseVatBook`         | Registro cronológico de todos los comprobantes de compra de un período fiscal.                           |
| Libro IVA Ventas             | `saleVatBook`             | Registro cronológico de todos los comprobantes de venta de un período fiscal.                            |
| Período fiscal               | `fiscalPeriod`            | Mes/año de un libro IVA. Formato: `YYYY-MM`.                                                            |
| Neto gravado                 | `netAmount`               | Monto del comprobante sujeto a IVA (base imponible).                                                    |
| Exento                       | `exemptAmount`            | Monto del comprobante no alcanzado por IVA.                                                              |
| Punto de venta               | `pointOfSale`             | Número que identifica el origen de emisión del comprobante (sucursal/punto de emisión).                  |
| Razón social                 | `businessName`            | Nombre legal de una persona jurídica o nombre completo de una persona física ante AFIP.                  |
| Condición fiscal              | `taxCondition`            | Categorización tributaria del contribuyente ante AFIP.                                                   |
| Domicilio fiscal              | `fiscalAddress`           | Domicilio registrado ante AFIP como domicilio fiscal del contribuyente.                                  |
| Algoritmo módulo 11           | `CuitValidator`           | Algoritmo de verificación del dígito verificador del CUIT. Implementado en `shared/fiscal/`.             |
| AFIP                          | —                         | Administración Federal de Ingresos Públicos. Organismo recaudador de Argentina. No se modela en el MVP. |

---

## Convenciones de Naming

### Clases Java

| Componente                  | Patrón                               | Ejemplo                          |
|-----------------------------|--------------------------------------|----------------------------------|
| Entidad de dominio          | `{Entity}`                           | `Taxpayer`, `Voucher`            |
| Enum de dominio             | `{Concepto}`                         | `TaxCondition`, `VoucherType`    |
| Repositorio (puerto)        | `{Entity}Repository`                 | `TaxpayerRepository`             |
| Use Case (command)          | `{Action}{Entity}UseCase`            | `CreateTaxpayerUseCase`          |
| Use Case (query)            | `Get{Entity}UseCase`                 | `GetTaxpayerUseCase`             |
| Service                     | `{Action}{Entity}Service`            | `CreateTaxpayerService`          |
| Spring Data Repository      | `SpringData{Entity}Repository`       | `SpringDataTaxpayerRepository`   |
| Repository Adapter          | `{Entity}RepositoryAdapter`          | `TaxpayerRepositoryAdapter`      |
| Controller                  | `{Entity}Controller`                 | `TaxpayerController`             |
| Command DTO (app)           | `{Action}{Entity}Command`            | `CreateTaxpayerCommand`          |
| Result DTO lista (app)      | `{Entity}ItemResult`                 | `TaxpayerItemResult`             |
| Result DTO detalle (app)    | `{Entity}DetailResult`               | `TaxpayerDetailResult`           |
| Request DTO (web)           | `Create{Entity}Request`              | `CreateTaxpayerRequest`          |
| Response DTO (web)          | `Create{Entity}Response`             | `CreateTaxpayerResponse`         |
| Web Mapper                  | `{Entity}WebMapper`                  | `TaxpayerWebMapper`              |
| Excepción de dominio        | `{Concepto}Exception`                | `DomainValidationException`      |

### Base de Datos

| Elemento      | Convención               | Ejemplo                    |
|---------------|--------------------------|----------------------------|
| Tablas        | `snake_case`, plural     | `taxpayers`, `vouchers`    |
| Columnas      | `snake_case`             | `business_name`, `cuit`    |
| FK            | `{tabla_singular}_id`    | `taxpayer_id`, `user_id`   |
| Índices       | `idx_{tabla}_{columnas}` | `idx_taxpayers_cuit`       |
| Unique        | `uk_{tabla}_{columnas}`  | `uk_taxpayers_cuit`        |

### Paquetes

```
com.{user}.taxmanagerapi.{boundedContext}.{capa}.{subcapa}
```

Ejemplo: `com.taxmanager.taxmanagerapi.taxpayer.domain.entity`

---

## Validación del CUIT — Algoritmo Módulo 11

El CUIT tiene el formato `XX-XXXXXXXX-X` (11 dígitos sin guiones):

- **Posiciones 1-2:** Tipo de contribuyente (20, 23, 24, 27, 30, 33, 34)
- **Posiciones 3-10:** Número de documento o identificador
- **Posición 11:** Dígito verificador (calculado con módulo 11)

**Almacenamiento:** `VARCHAR(11)` sin guiones en la base de datos.
**API:** Se expone con guiones (`20-12345678-3`) en requests y responses.
**Validación:** Se implementa en `shared/fiscal/CuitValidator.java`.

---

## Relación entre Entidades

```
User (1) ──── (N) RefreshToken
Taxpayer (1) ──── (N) Voucher
```

- Un **User** puede tener múltiples **RefreshTokens** activos (uno por dispositivo/sesión).
- Un **Taxpayer** puede tener múltiples **Vouchers** (comprobantes de compra y venta).
- **VatBook** no es una entidad — es un reporte generado a partir de los vouchers.

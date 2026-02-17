# Especificación de API — Tax Manager API

Documentación de todos los endpoints REST del MVP. Incluye URLs, métodos HTTP,
request/response de ejemplo, códigos de estado y reglas de autorización.

> **Base URL:** `/api/v1`
>
> **Formato:** JSON (`application/json`)
>
> **Autenticación:** Bearer Token (JWT) en el header `Authorization`, excepto
> los endpoints de auth marcados como públicos.
>
> **Errores:** Formato [RFC 7807 Problem Detail](https://www.rfc-editor.org/rfc/rfc7807)
> en todas las respuestas de error.

---

## Índice de Endpoints

| Módulo     | Método   | Endpoint                                    | Descripción                    | Auth     |
|------------|----------|---------------------------------------------|--------------------------------|----------|
| Auth       | `POST`   | `/auth/register`                            | Registrar usuario              | Público  |
| Auth       | `POST`   | `/auth/login`                               | Iniciar sesión                 | Público  |
| Auth       | `POST`   | `/auth/refresh`                             | Renovar access token           | Público  |
| Auth       | `POST`   | `/auth/logout`                              | Cerrar sesión                  | Bearer   |
| Taxpayer   | `POST`   | `/taxpayers`                                | Crear contribuyente            | Bearer   |
| Taxpayer   | `GET`    | `/taxpayers`                                | Listar contribuyentes          | Bearer   |
| Taxpayer   | `GET`    | `/taxpayers/{id}`                           | Detalle de contribuyente       | Bearer   |
| Taxpayer   | `PUT`    | `/taxpayers/{id}`                           | Actualizar contribuyente       | Bearer   |
| Taxpayer   | `DELETE` | `/taxpayers/{id}`                           | Desactivar contribuyente       | Bearer   |
| Taxpayer   | `GET`    | `/taxpayers/search?cuit=XX-XXXXXXXX-X`      | Buscar por CUIT                | Bearer   |
| Voucher    | `POST`   | `/taxpayers/{taxpayerId}/vouchers`          | Crear comprobante              | Bearer   |
| Voucher    | `GET`    | `/taxpayers/{taxpayerId}/vouchers`          | Listar comprobantes            | Bearer   |
| Voucher    | `GET`    | `/vouchers/{id}`                            | Detalle de comprobante         | Bearer   |
| VAT Book   | `GET`    | `/vat-books/purchases?period=YYYY-MM`       | Libro IVA Compras              | Bearer   |
| VAT Book   | `GET`    | `/vat-books/sales?period=YYYY-MM`           | Libro IVA Ventas               | Bearer   |

---

## Auth

### `POST /api/v1/auth/register`

Registra un nuevo usuario en el sistema.

**Autorización:** Público (no requiere token)

**Request:**

```json
{
  "email": "contador@estudio.com",
  "password": "SecurePass123!",
  "fullName": "Juan Pérez"
}
```

**Response `201 Created`:**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "email": "contador@estudio.com",
  "fullName": "Juan Pérez",
  "role": "ACCOUNTANT",
  "createdAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación                        |
|--------|----------------------------------|
| `400`  | Campos inválidos o faltantes     |
| `409`  | Email ya registrado              |

**Notas:**
- El rol por defecto al registrarse es `ACCOUNTANT`.
- Solo un `ADMIN` puede cambiar roles (funcionalidad futura).
- La contraseña debe tener mínimo 8 caracteres.

---

### `POST /api/v1/auth/login`

Autentica un usuario y devuelve access token + refresh token.

**Autorización:** Público

**Request:**

```json
{
  "email": "contador@estudio.com",
  "password": "SecurePass123!"
}
```

**Response `200 OK`:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "d4e5f6a7-b8c9-0123-d456-e789f0123456",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Códigos de error:**

| Código | Situación                        |
|--------|----------------------------------|
| `400`  | Campos inválidos o faltantes     |
| `401`  | Credenciales incorrectas         |

**Notas:**
- `expiresIn` está en segundos (900 = 15 minutos).
- El `refreshToken` es un UUID opaco almacenado en la tabla `refresh_tokens`.
- El `accessToken` es un JWT firmado con HS256 que contiene `sub` (userId), `email` y `role`.

---

### `POST /api/v1/auth/refresh`

Renueva el access token usando un refresh token válido.

**Autorización:** Público (se autentica con el refresh token en el body)

**Request:**

```json
{
  "refreshToken": "d4e5f6a7-b8c9-0123-d456-e789f0123456"
}
```

**Response `200 OK`:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "f0123456-a7b8-c9d0-1234-56e789f01234",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Códigos de error:**

| Código | Situación                                  |
|--------|--------------------------------------------|
| `400`  | Refresh token faltante                     |
| `401`  | Refresh token inválido, expirado o revocado|

**Notas:**
- Se implementa **refresh token rotation**: al usarse, el refresh token anterior se
  revoca y se emite uno nuevo. Esto mitiga el riesgo de robo de refresh tokens.

---

### `POST /api/v1/auth/logout`

Revoca el refresh token del usuario, invalidando la sesión.

**Autorización:** Bearer Token

**Request:**

```json
{
  "refreshToken": "d4e5f6a7-b8c9-0123-d456-e789f0123456"
}
```

**Response `200 OK`:**

```json
{
  "message": "Sesión cerrada correctamente"
}
```

**Códigos de error:**

| Código | Situación                          |
|--------|------------------------------------|
| `400`  | Refresh token faltante             |
| `401`  | Access token inválido o ausente    |

**Notas:**
- Marca el refresh token como `revoked = true` en la base de datos.
- El access token sigue siendo válido hasta que expire (es stateless).

---

## Taxpayer (Contribuyentes)

### `POST /api/v1/taxpayers`

Crea un nuevo contribuyente.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`)

**Request:**

```json
{
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Corrientes 1234, CABA, Buenos Aires",
  "email": "contacto@lopez-srl.com.ar",
  "phone": "011-4567-8901"
}
```

**Response `201 Created`:**

```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Corrientes 1234, CABA, Buenos Aires",
  "email": "contacto@lopez-srl.com.ar",
  "phone": "011-4567-8901",
  "active": true,
  "createdAt": "2026-02-16T10:30:00",
  "updatedAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación                                       |
|--------|-------------------------------------------------|
| `400`  | Campos inválidos o faltantes                    |
| `409`  | CUIT ya registrado                              |
| `422`  | CUIT no pasa validación módulo 11               |

> **Nota sobre 422:** Se usa `422 Unprocessable Entity` para errores de negocio
> (validación de dominio) vs `400 Bad Request` para errores de formato/sintaxis
> (Bean Validation).

---

### `GET /api/v1/taxpayers`

Lista contribuyentes activos con paginación.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Query Parameters:**

| Parámetro | Tipo   | Default | Descripción              |
|-----------|--------|---------|--------------------------|
| `page`    | `int`  | `0`     | Número de página (0-based)|
| `size`    | `int`  | `20`    | Elementos por página     |
| `sort`    | `string` | `businessName,asc` | Campo y dirección de orden |

**Response `200 OK`:**

```json
{
  "content": [
    {
      "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
      "businessName": "Distribuidora López S.R.L.",
      "cuit": "30-71234567-9",
      "taxCondition": "RESPONSABLE_INSCRIPTO",
      "active": true
    },
    {
      "id": "c3d4e5f6-a7b8-9012-cdef-345678901234",
      "businessName": "María García — Monotributo",
      "cuit": "27-28456789-3",
      "taxCondition": "MONOTRIBUTISTA",
      "active": true
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

**Notas:**
- El listado devuelve `{Entity}ItemResult` (versión resumida, sin domicilio ni contacto).
- Solo muestra contribuyentes activos (`active = true`).
- La estructura de paginación sigue el formato de Spring Data `PagedModel`.

---

### `GET /api/v1/taxpayers/{id}`

Obtiene el detalle completo de un contribuyente.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Response `200 OK`:**

```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Corrientes 1234, CABA, Buenos Aires",
  "email": "contacto@lopez-srl.com.ar",
  "phone": "011-4567-8901",
  "active": true,
  "createdAt": "2026-02-16T10:30:00",
  "updatedAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación               |
|--------|-------------------------|
| `404`  | Contribuyente no existe |

---

### `PUT /api/v1/taxpayers/{id}`

Actualiza un contribuyente existente.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`)

**Request:**

```json
{
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Rivadavia 5678, CABA, Buenos Aires",
  "email": "nuevo-contacto@lopez-srl.com.ar",
  "phone": "011-9876-5432"
}
```

**Response `200 OK`:**

```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Rivadavia 5678, CABA, Buenos Aires",
  "email": "nuevo-contacto@lopez-srl.com.ar",
  "phone": "011-9876-5432",
  "active": true,
  "createdAt": "2026-02-16T10:30:00",
  "updatedAt": "2026-02-16T11:45:00"
}
```

**Códigos de error:**

| Código | Situación                                       |
|--------|-------------------------------------------------|
| `400`  | Campos inválidos o faltantes                    |
| `404`  | Contribuyente no existe                         |
| `409`  | Nuevo CUIT ya pertenece a otro contribuyente    |
| `422`  | CUIT no pasa validación módulo 11               |

**Notas:**
- El PUT es **full update** (todos los campos requeridos deben enviarse).
- Si el CUIT cambió, se re-valida unicidad y módulo 11.

---

### `DELETE /api/v1/taxpayers/{id}`

Desactiva un contribuyente (soft delete).

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`)

**Response `204 No Content`**

(Sin cuerpo de respuesta)

**Códigos de error:**

| Código | Situación               |
|--------|-------------------------|
| `404`  | Contribuyente no existe |

**Notas:**
- Ejecuta un soft delete: pone `active = false`.
- Los comprobantes asociados **no se eliminan ni desactivan**.
- Un contribuyente desactivado no aparece en listados ni búsquedas.

---

### `GET /api/v1/taxpayers/search?cuit=XX-XXXXXXXX-X`

Busca un contribuyente activo por CUIT exacto.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Query Parameters:**

| Parámetro | Tipo     | Requerido | Descripción                     |
|-----------|----------|-----------|---------------------------------|
| `cuit`    | `string` | Sí        | CUIT con guiones (XX-XXXXXXXX-X)|

**Response `200 OK`:**

```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "businessName": "Distribuidora López S.R.L.",
  "cuit": "30-71234567-9",
  "taxCondition": "RESPONSABLE_INSCRIPTO",
  "fiscalAddress": "Av. Corrientes 1234, CABA, Buenos Aires",
  "email": "contacto@lopez-srl.com.ar",
  "phone": "011-4567-8901",
  "active": true,
  "createdAt": "2026-02-16T10:30:00",
  "updatedAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación                                |
|--------|------------------------------------------|
| `400`  | Parámetro `cuit` faltante o mal formado  |
| `404`  | No se encontró contribuyente con ese CUIT|

**Notas:**
- La búsqueda es por CUIT exacto (no parcial).
- Solo busca entre contribuyentes activos.
- El CUIT se envía con guiones en la URL y se normaliza (elimina guiones) para la
  búsqueda en BD.

---

## Voucher (Comprobantes)

### `POST /api/v1/taxpayers/{taxpayerId}/vouchers`

Registra un nuevo comprobante de compra o venta para un contribuyente.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`)

**Path Parameters:**

| Parámetro    | Tipo   | Descripción                |
|--------------|--------|----------------------------|
| `taxpayerId` | `UUID` | ID del contribuyente       |

**Request:**

```json
{
  "category": "SALE",
  "voucherType": "FACTURA_A",
  "issueDate": "2026-02-15",
  "pointOfSale": 1,
  "voucherNumber": 1542,
  "netAmount": 100000.0000,
  "vatAmount": 21000.0000,
  "exemptAmount": 0.0000,
  "totalAmount": 121000.0000,
  "description": "Servicios de consultoría febrero 2026"
}
```

**Response `201 Created`:**

```json
{
  "id": "d4e5f6a7-b8c9-0123-d456-e789f0123456",
  "taxpayerId": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "taxpayerBusinessName": "Distribuidora López S.R.L.",
  "taxpayerCuit": "30-71234567-9",
  "category": "SALE",
  "voucherType": "FACTURA_A",
  "issueDate": "2026-02-15",
  "pointOfSale": 1,
  "voucherNumber": 1542,
  "netAmount": 100000.0000,
  "vatAmount": 21000.0000,
  "exemptAmount": 0.0000,
  "totalAmount": 121000.0000,
  "description": "Servicios de consultoría febrero 2026",
  "createdAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación                                           |
|--------|------------------------------------------------------|
| `400`  | Campos inválidos o faltantes                         |
| `404`  | Contribuyente no existe                              |
| `422`  | Montos no cuadran (`net + vat + exempt ≠ total`)     |
| `422`  | Montos negativos u otras reglas de negocio violadas  |

---

### `GET /api/v1/taxpayers/{taxpayerId}/vouchers`

Lista los comprobantes de un contribuyente con paginación.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Path Parameters:**

| Parámetro    | Tipo   | Descripción                |
|--------------|--------|----------------------------|
| `taxpayerId` | `UUID` | ID del contribuyente       |

**Query Parameters:**

| Parámetro  | Tipo     | Default              | Descripción                   |
|------------|----------|----------------------|-------------------------------|
| `page`     | `int`    | `0`                  | Número de página              |
| `size`     | `int`    | `20`                 | Elementos por página          |
| `sort`     | `string` | `issueDate,desc`     | Campo y dirección de orden    |
| `category` | `string` | —                    | Filtro: `PURCHASE` o `SALE`   |

**Response `200 OK`:**

```json
{
  "content": [
    {
      "id": "d4e5f6a7-b8c9-0123-d456-e789f0123456",
      "category": "SALE",
      "voucherType": "FACTURA_A",
      "issueDate": "2026-02-15",
      "pointOfSale": 1,
      "voucherNumber": 1542,
      "totalAmount": 121000.0000,
      "description": "Servicios de consultoría febrero 2026"
    },
    {
      "id": "e5f6a7b8-c9d0-1234-e567-f89012345678",
      "category": "PURCHASE",
      "voucherType": "FACTURA_A",
      "issueDate": "2026-02-10",
      "pointOfSale": 3,
      "voucherNumber": 8871,
      "totalAmount": 55000.0000,
      "description": "Compra de insumos"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

**Notas:**
- El listado devuelve una versión resumida (sin desglose de montos).
- Se puede filtrar opcionalmente por `category` (PURCHASE/SALE).

---

### `GET /api/v1/vouchers/{id}`

Obtiene el detalle completo de un comprobante.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Response `200 OK`:**

```json
{
  "id": "d4e5f6a7-b8c9-0123-d456-e789f0123456",
  "taxpayerId": "b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "taxpayerBusinessName": "Distribuidora López S.R.L.",
  "taxpayerCuit": "30-71234567-9",
  "category": "SALE",
  "voucherType": "FACTURA_A",
  "issueDate": "2026-02-15",
  "pointOfSale": 1,
  "voucherNumber": 1542,
  "netAmount": 100000.0000,
  "vatAmount": 21000.0000,
  "exemptAmount": 0.0000,
  "totalAmount": 121000.0000,
  "description": "Servicios de consultoría febrero 2026",
  "createdAt": "2026-02-16T10:30:00",
  "updatedAt": "2026-02-16T10:30:00"
}
```

**Códigos de error:**

| Código | Situación               |
|--------|-------------------------|
| `404`  | Comprobante no existe   |

**Notas:**
- Incluye datos del contribuyente (nombre y CUIT) para contexto.

---

## VAT Book (Libros IVA)

### `GET /api/v1/vat-books/purchases?period=YYYY-MM`

Genera el libro IVA Compras para un período fiscal.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Query Parameters:**

| Parámetro | Tipo     | Requerido | Descripción                           |
|-----------|----------|-----------|---------------------------------------|
| `period`  | `string` | Sí        | Período fiscal en formato `YYYY-MM`   |

**Response `200 OK`:**

```json
{
  "period": "2026-02",
  "category": "PURCHASE",
  "vouchers": [
    {
      "id": "e5f6a7b8-c9d0-1234-e567-f89012345678",
      "issueDate": "2026-02-03",
      "voucherType": "FACTURA_A",
      "pointOfSale": 5,
      "voucherNumber": 3201,
      "taxpayerBusinessName": "Proveedor ABC S.A.",
      "taxpayerCuit": "30-65432198-7",
      "netAmount": 80000.0000,
      "vatAmount": 16800.0000,
      "exemptAmount": 0.0000,
      "totalAmount": 96800.0000
    },
    {
      "id": "f6a7b8c9-d012-3456-f789-012345678901",
      "issueDate": "2026-02-10",
      "voucherType": "FACTURA_A",
      "pointOfSale": 3,
      "voucherNumber": 8871,
      "taxpayerBusinessName": "Distribuidora XYZ S.R.L.",
      "taxpayerCuit": "30-71234567-9",
      "netAmount": 55000.0000,
      "vatAmount": 11550.0000,
      "exemptAmount": 0.0000,
      "totalAmount": 66550.0000
    }
  ],
  "totals": {
    "netAmount": 135000.0000,
    "vatAmount": 28350.0000,
    "exemptAmount": 0.0000,
    "totalAmount": 163350.0000
  },
  "voucherCount": 2
}
```

**Códigos de error:**

| Código | Situación                          |
|--------|------------------------------------|
| `400`  | Formato de período inválido        |

**Notas:**
- Los comprobantes se listan en orden cronológico (`issueDate` ascendente).
- Si no hay comprobantes en el período, devuelve la estructura con lista vacía y
  totales en cero.
- Incluye datos del contribuyente en cada comprobante para el formato del libro.
- Este endpoint **no tiene paginación** — los libros IVA se generan completos por
  período (un mes típico tiene decenas a cientos de comprobantes, no miles).

---

### `GET /api/v1/vat-books/sales?period=YYYY-MM`

Genera el libro IVA Ventas para un período fiscal. Idéntico al de compras pero filtra
por `category = SALE`.

**Autorización:** Bearer Token (roles: `ADMIN`, `ACCOUNTANT`, `VIEWER`)

**Query Parameters:**

| Parámetro | Tipo     | Requerido | Descripción                           |
|-----------|----------|-----------|---------------------------------------|
| `period`  | `string` | Sí        | Período fiscal en formato `YYYY-MM`   |

**Response `200 OK`:**

```json
{
  "period": "2026-02",
  "category": "SALE",
  "vouchers": [
    {
      "id": "d4e5f6a7-b8c9-0123-d456-e789f0123456",
      "issueDate": "2026-02-15",
      "voucherType": "FACTURA_A",
      "pointOfSale": 1,
      "voucherNumber": 1542,
      "taxpayerBusinessName": "Distribuidora López S.R.L.",
      "taxpayerCuit": "30-71234567-9",
      "netAmount": 100000.0000,
      "vatAmount": 21000.0000,
      "exemptAmount": 0.0000,
      "totalAmount": 121000.0000
    }
  ],
  "totals": {
    "netAmount": 100000.0000,
    "vatAmount": 21000.0000,
    "exemptAmount": 0.0000,
    "totalAmount": 121000.0000
  },
  "voucherCount": 1
}
```

**Códigos de error:**

| Código | Situación                     |
|--------|-------------------------------|
| `400`  | Formato de período inválido   |

---

## Formato de Errores — RFC 7807 Problem Detail

Todas las respuestas de error usan el formato estándar `ProblemDetail` de Spring 6+
(implementación nativa de RFC 7807).

### Ejemplo: Error de validación (`400 Bad Request`)

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Error de validación en los campos enviados",
  "instance": "/api/v1/taxpayers",
  "errors": [
    {
      "field": "businessName",
      "message": "no debe estar vacío"
    },
    {
      "field": "cuit",
      "message": "no debe estar vacío"
    }
  ]
}
```

### Ejemplo: Recurso no encontrado (`404 Not Found`)

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Contribuyente no encontrado con id: b2c3d4e5-f6a7-8901-bcde-f23456789012",
  "instance": "/api/v1/taxpayers/b2c3d4e5-f6a7-8901-bcde-f23456789012"
}
```

### Ejemplo: Conflicto (`409 Conflict`)

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Ya existe un contribuyente con CUIT 30-71234567-9",
  "instance": "/api/v1/taxpayers"
}
```

### Ejemplo: Error de dominio (`422 Unprocessable Entity`)

```json
{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "El CUIT 30-71234567-0 no es válido (dígito verificador incorrecto)",
  "instance": "/api/v1/taxpayers"
}
```

### Ejemplo: No autenticado (`401 Unauthorized`)

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Token de acceso inválido o expirado",
  "instance": "/api/v1/taxpayers"
}
```

### Ejemplo: Sin permisos (`403 Forbidden`)

```json
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "No tiene permisos para realizar esta acción",
  "instance": "/api/v1/taxpayers"
}
```

---

## Convenciones Generales

### Headers requeridos

| Header          | Valor                  | Notas                              |
|-----------------|------------------------|------------------------------------|
| `Content-Type`  | `application/json`     | En requests con body               |
| `Authorization` | `Bearer {accessToken}` | En endpoints protegidos            |

### Paginación

Los endpoints con listados usan la paginación estándar de Spring Data:

- **Request:** `?page=0&size=20&sort=campo,asc`
- **Response:** Objeto con `content` (array de items) y `page` (metadatos de paginación)

### Formato de fechas

| Tipo          | Formato              | Ejemplo                  |
|---------------|----------------------|--------------------------|
| `LocalDate`   | `YYYY-MM-DD`         | `2026-02-15`             |
| `LocalDateTime` | `YYYY-MM-DD'T'HH:mm:ss` | `2026-02-16T10:30:00` |
| Período fiscal | `YYYY-MM`           | `2026-02`                |

### Montos

- Se envían y reciben como números con hasta 4 decimales.
- El separador decimal es el punto (`.`).
- Ejemplo: `100000.0000`, `21000.5000`, `0.0000`.

### CUIT

- En la API siempre se envía y recibe **con guiones**: `XX-XXXXXXXX-X`.
- Ejemplo: `30-71234567-9`, `20-12345678-3`.
- Internamente se almacena sin guiones en la BD.

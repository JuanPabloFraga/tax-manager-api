# ADR-004: JWT Stateless con Refresh Token en Base de Datos

**Estado:** Aceptado
**Fecha:** 2026-02-16

## Contexto

El proyecto necesita autenticación para proteger los endpoints de la API. Los usuarios
(contadores) se registran, inician sesión, y deben poder cerrar sesión de forma segura.

Necesitamos decidir el mecanismo de autenticación y cómo manejar la duración de las
sesiones y el logout.

## Decisión

Implementamos **JWT stateless** con la librería `jjwt`, combinando dos tipos de token:

| Token         | Tipo        | Duración    | Almacenamiento           | Revocable |
|---------------|-------------|-------------|--------------------------|-----------|
| Access Token  | JWT (HS256) | 15 minutos  | Solo en el cliente       | No (expira solo) |
| Refresh Token | UUID opaco  | 7 días      | Base de datos (`refresh_tokens`) | Sí |

**Flujo:**

1. **Login:** Devuelve access token + refresh token.
2. **Requests:** El cliente envía el access token en `Authorization: Bearer {token}`.
3. **Refresh:** Cuando el access token expira, el cliente envía el refresh token
   para obtener uno nuevo. Se aplica **refresh token rotation** (el anterior se
   revoca y se emite uno nuevo).
4. **Logout:** Marca el refresh token como `revoked = true` en la BD. El access
   token sigue válido hasta que expire (~15 min).

**Claims del JWT (access token):**

```json
{
  "sub": "a1b2c3d4-e5f6-...",
  "email": "contador@estudio.com",
  "role": "ACCOUNTANT",
  "iat": 1740000000,
  "exp": 1740000900
}
```

## Alternativas Consideradas

### A) Session-based (cookies + sesión en servidor)
- **Pro:** Logout instantáneo, familiar.
- **Contra:** Stateful, requiere sesión en memoria o Redis. No apto para APIs REST
  puras. Complicado para clientes móviles o SPA.

### B) JWT puro sin refresh token
- **Pro:** Totalmente stateless, sin BD para tokens.
- **Contra:** Si el token expira, el usuario tiene que re-loguearse. Si se alarga
  la duración del JWT, un token robado es peligroso. No hay forma de hacer logout.

### C) JWT + refresh token en BD (elegida)
- **Pro:** Access token corto (15 min) limita el daño de un robo. Refresh token
  permite sesiones largas sin comprometer seguridad. Logout efectivo al revocar
  el refresh token.
- **Contra:** El refresh token agrega un acceso a BD en el refresh endpoint.
  Aceptable porque el refresh ocurre cada 15 minutos, no en cada request.

### D) Spring Security OAuth2 Resource Server
- **Pro:** Integración nativa con Spring, estándar.
- **Contra:** Requiere un Authorization Server (Keycloak, Auth0, etc.) o montar
  uno propio. Sobreingeniería para un MVP con un solo tipo de aplicación cliente.

## Consecuencias

- **Positivas:**
  - Autenticación stateless en la mayoría de requests (solo valida el JWT).
  - Sesiones de larga duración sin comprometer seguridad.
  - Logout funcional al revocar refresh tokens.
  - Refresh token rotation mitiga robo de tokens.

- **Negativas:**
  - Un access token robado es válido hasta que expire (máx 15 min).
  - La tabla `refresh_tokens` crece y necesita limpieza periódica de tokens
    expirados/revocados. Se puede hacer con un job programado (futuro).
  - No implementamos blacklist de access tokens (complejidad no justificada
    para el MVP).

- **Configuración externalizada:**
  - `jwt.secret` → clave HS256 (mínimo 256 bits).
  - `jwt.access-token.expiration` → 900000 ms (15 min).
  - `jwt.refresh-token.expiration` → 604800000 ms (7 días).
  - Todas configurables vía `application.yml` o variables de entorno.

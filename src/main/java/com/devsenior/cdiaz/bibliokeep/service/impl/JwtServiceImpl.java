package com.devsenior.cdiaz.bibliokeep.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.devsenior.cdiaz.bibliokeep.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar operaciones JWT (JSON Web Token).
 * Proporciona métodos para crear, validar y extraer información de tokens JWT.
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    /**
     * Obtiene la clave secreta en formato SecretKey.
     *
     * @return SecretKey para firmar y validar tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT con los datos proporcionados.
     *
     * @param claims  Mapa con los datos a incluir en el token
     * @param subject Asunto del token (generalmente el identificador del usuario)
     * @return Token JWT generado
     */
    @Override
    public String generateToken(Map<String, Object> claims, String subject) {
        return buildToken(claims, subject, expirationTime);
    }

    /**
     * Genera un token JWT con los datos proporcionados y tiempo de expiración
     * personalizado.
     *
     * @param claims           Mapa con los datos a incluir en el token
     * @param subject          Asunto del token (generalmente el identificador del
     *                         usuario)
     * @param expirationTimeMs Tiempo de expiración en milisegundos
     * @return Token JWT generado
     */
    @Override
    public String generateToken(Map<String, Object> claims, String subject, long expirationTimeMs) {
        return buildToken(claims, subject, expirationTimeMs);
    }

    /**
     * Genera un token JWT simple con solo el subject (identificador).
     *
     * @param subject Asunto del token (generalmente el identificador del usuario)
     * @return Token JWT generado
     */
    @Override
    public String generateToken(String subject) {
        return generateToken(new HashMap<>(), subject);
    }

    /**
     * Construye un token JWT con los parámetros especificados.
     *
     * @param claims           Mapa con los datos a incluir en el token
     * @param subject          Asunto del token
     * @param expirationTimeMs Tiempo de expiración en milisegundos
     * @return Token JWT generado
     */
    private String buildToken(Map<String, Object> claims, String subject, long expirationTimeMs) {
        try {
            Instant now = Instant.now();
            Instant expirationDate = now.plusMillis(expirationTimeMs);

            return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(java.util.Date.from(now))
                    .expiration(java.util.Date.from(expirationDate))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            log.error("Error al generar el token JWT", e);
            throw new RuntimeException("Error al generar el token JWT", e);
        }
    }

    /**
     * Valida si un token JWT es válido y no ha expirado.
     *
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vacío o nulo: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Error en la firma del token JWT: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error al validar el token JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae todos los claims de un token JWT.
     *
     * @param token Token JWT del cual extraer los claims
     * @return Claims del token
     * @throws RuntimeException si el token es inválido
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error al extraer los claims del token JWT: {}", e.getMessage());
            throw new RuntimeException("Error al extraer los claims del token JWT", e);
        }
    }

    /**
     * Extrae un claim específico del token JWT.
     *
     * @param token    Token JWT del cual extraer el claim
     * @param claimKey Clave del claim a extraer
     * @return Valor del claim
     * @throws RuntimeException si el token es inválido o el claim no existe
     */
    @Override
    public <T> T getClaim(String token, String claimKey, Class<T> clazz) {
        Claims claims = getClaims(token);
        return claims.get(claimKey, clazz);
    }

    /**
     * Extrae un claim específico del token JWT.
     *
     * @param token    Token JWT del cual extraer el claim
     * @param claimKey Clave del claim a extraer
     * @return Valor del claim
     * @throws RuntimeException si el token es inválido o el claim no existe
     */
    @Override
    public <T> List<T> getClaimList(String token, String claimKey, Class<T> clazz) {
        var claims = getClaims(token);

        var value = claims.get(claimKey);

        if (value == null) {
            return List.of();
        }

        if (value instanceof List<?> raw) {
            return raw.stream()
                    .map(clazz::cast)
                    .toList();
        }

        return List.of();
    }

    /**
     * Extrae el subject (identificador del usuario) del token JWT.
     *
     * @param token Token JWT
     * @return Subject del token
     */
    @Override
    public String getSubject(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Error al extraer el subject del token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el tiempo de expiración configurado en milisegundos.
     *
     * @return Tiempo de expiración en milisegundos
     */
    public long getExpirationTime() {
        return expirationTime;
    }

}

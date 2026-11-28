package com.aulaverde.aulaverde.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Obtener clave secreta
    private SecretKey obtenerClave() {

        byte[] clave = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(clave);
    }

    // Generar token
    public String generarToken(
            Integer usuarioId,
            String correo,
            String rol) {

        return Jwts.builder()
                .subject(correo)
                .claim("usuarioId", usuarioId)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + expiration))
                .signWith(obtenerClave())
                .compact();
    }

    // Obtener información del token
    private Claims obtenerClaims(String token) {

        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Obtener correo
    public String obtenerCorreo(String token) {

        return obtenerClaims(token).getSubject();
    }

    // Obtener ID del usuario
    public Integer obtenerUsuarioId(String token) {

        return obtenerClaims(token)
                .get("usuarioId", Integer.class);
    }

    // Obtener rol
    public String obtenerRol(String token) {

        return obtenerClaims(token)
                .get("rol", String.class);
    }

    // Validar token
    public boolean validarToken(String token) {

        try {

            obtenerClaims(token);
            return true;

        } catch (RuntimeException e) {

            return false;
        }
    }
}
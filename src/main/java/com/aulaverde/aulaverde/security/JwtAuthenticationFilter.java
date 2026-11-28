package com.aulaverde.aulaverde.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Interceptar peticiones HTTP
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Obtener encabezado de autenticación
        String authorization = request.getHeader("Authorization");

        // Comprobar si existe un token Bearer
        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token JWT
        String token = authorization.substring(7);

        // Validar token
        if (jwtService.validarToken(token)
                && SecurityContextHolder.getContext()
                        .getAuthentication() == null) {

            String correo = jwtService.obtenerCorreo(token);

            try {

                // Obtener usuario desde la base de datos
                UserDetails usuario =
                        userDetailsService.loadUserByUsername(correo);

                // Comprobar que el usuario esté habilitado
                if (usuario.isEnabled()) {

                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities());

                    autenticacion.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    // Registrar usuario autenticado
                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacion);
                }

            } catch (UsernameNotFoundException e) {

                SecurityContextHolder.clearContext();
            }
        }

        // Continuar con la petición
        filterChain.doFilter(request, response);
    }
}
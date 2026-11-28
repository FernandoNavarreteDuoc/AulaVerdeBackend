package com.aulaverde.aulaverde.security;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // Desactivar CSRF para autenticación JWT
            .csrf(csrf -> csrf.disable())

            // No utilizar sesiones HTTP
            .sessionManagement(session -> session
                    .sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            // Desactivar formulario de inicio de sesión
            .formLogin(form -> form.disable())

            // Desactivar autenticación HTTP Basic
            .httpBasic(basic -> basic.disable())

            // Configurar permisos de acceso
            .authorizeHttpRequests(auth -> auth

                // Registro e inicio de sesión
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/auth/registro",
                        "/api/auth/login"
                ).permitAll()

                // Permitir acceso a Swagger
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                // Consultar roles
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/roles",
                        "/api/roles/**"
                ).authenticated()

                // Bloquear gestión de usuarios
                // hasta implementar permisos individuales
                .requestMatchers(
                        "/api/usuarios",
                        "/api/usuarios/**"
                ).denyAll()

                // Bloquear recursos hasta comprobar propietarios
                .requestMatchers(
                        "/api/cursos",
                        "/api/cursos/**",
                        "/api/rubricas",
                        "/api/rubricas/**",
                        "/api/matriculas",
                        "/api/matriculas/**",
                        "/api/evaluaciones",
                        "/api/evaluaciones/**",
                        "/api/calificaciones",
                        "/api/calificaciones/**",
                        "/api/documentos",
                        "/api/documentos/**"
                ).denyAll()

                // Bloquear cualquier endpoint no autorizado
                .anyRequest().denyAll()
            )

            // Configurar respuestas de seguridad
            .exceptionHandling(exception -> exception

                .authenticationEntryPoint(
                    (request, response, error) ->
                        response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED))

                .accessDeniedHandler(
                    (request, response, error) ->
                        response.sendError(
                            HttpServletResponse.SC_FORBIDDEN))
            )

            // Agregar filtro JWT
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
package com.aulaverde.aulaverde.security;

import jakarta.servlet.DispatcherType;
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

                // Permitir respuestas de error
                .dispatcherTypeMatchers(
                        DispatcherType.ERROR
                ).permitAll()

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
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml"
                ).permitAll()

                // Consultar roles
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/roles",
                        "/api/roles/**"
                ).authenticated()

                // Permitir acceso a usuarios autenticados
                .requestMatchers(
                        "/api/usuarios",
                        "/api/usuarios/**"
                ).authenticated()

                // Permitir consultar y descargar documentos
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/documentos",
                        "/api/documentos/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR",
                        "ESTUDIANTE"
                )

                // Permitir gestionar documentos
                .requestMatchers(
                        "/api/documentos",
                        "/api/documentos/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

                // Permitir acceso a cursos con autenticación
                .requestMatchers(
                        "/api/cursos",
                        "/api/cursos/**"
                ).authenticated()

                // Permitir consultar rúbricas de evaluaciones
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/rubricas/evaluacion/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR",
                        "ESTUDIANTE"
                )

                // Permitir gestionar rúbricas
                .requestMatchers(
                        "/api/rubricas",
                        "/api/rubricas/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

                // Permitir consultar matrículas
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/matriculas",
                        "/api/matriculas/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR",
                        "ESTUDIANTE"
                )

                // Permitir gestionar matrículas
                .requestMatchers(
                        "/api/matriculas",
                        "/api/matriculas/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

                // Permitir consultar evaluaciones
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/evaluaciones",
                        "/api/evaluaciones/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR",
                        "ESTUDIANTE"
                )

                // Permitir gestionar evaluaciones
                .requestMatchers(
                        "/api/evaluaciones",
                        "/api/evaluaciones/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

                // Permitir consultar calificaciones
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/calificaciones",
                        "/api/calificaciones/**"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR",
                        "ESTUDIANTE"
                )

                // Permitir registrar calificaciones
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/calificaciones"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

                // Permitir verificar Object Storage
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/storage/estado"
                ).hasAnyRole(
                        "DOCENTE",
                        "INSTRUCTOR"
                )

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
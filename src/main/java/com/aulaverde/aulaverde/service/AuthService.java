package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.LoginDTO;
import com.aulaverde.aulaverde.DTO.RegistroDTO;
import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.DTO.RolDTO;
import com.aulaverde.aulaverde.DTO.LoginResponseDTO;

import com.aulaverde.aulaverde.model.Usuario;
import com.aulaverde.aulaverde.model.Rol;

import com.aulaverde.aulaverde.repository.UsuarioRepository;
import com.aulaverde.aulaverde.repository.RolRepository;

import com.aulaverde.aulaverde.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private RolRepository rolRepository;

        @Autowired
        private JwtService jwtService;

        private final BCryptPasswordEncoder passwordEncoder =
                new BCryptPasswordEncoder();

        // Registrar usuario
        public UsuarioDTO registrar(RegistroDTO dto) {

                String correo = dto.getCorreo().trim().toLowerCase();

                // Comprobar si el correo ya existe
                if (usuarioRepository.existsByCorreo(correo)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El correo ya está registrado");
                }

                // Buscar rol predeterminado
                Rol rol = rolRepository.findByNombre("ESTUDIANTE")
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rol no encontrado"));

                // Crear usuario
                Usuario usuario = new Usuario();

                usuario.setNombre(dto.getNombre());
                usuario.setApellido(dto.getApellido());
                usuario.setCorreo(correo);

                // Proteger contraseña mediante BCrypt
                usuario.setContrasena(
                        passwordEncoder.encode(dto.getContrasena()));

                usuario.setActivo(true);
                usuario.setRol(rol);

                return convertirDTO(usuarioRepository.save(usuario));
        }

        // Iniciar sesión
        public LoginResponseDTO login(LoginDTO dto) {

                // Buscar usuario por correo
                Usuario usuario = usuarioRepository
                        .findByCorreo(dto.getCorreo().trim().toLowerCase())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Correo o contraseña incorrectos"));

                // Validar contraseña
                if (!passwordEncoder.matches(
                        dto.getContrasena(),
                        usuario.getContrasena())) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos");
                }

                // Validar estado del usuario
                if (!Boolean.TRUE.equals(usuario.getActivo())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "El usuario está desactivado");
                }

                // Generar token JWT
                String token = jwtService.generarToken(
                        usuario.getId(),
                        usuario.getCorreo(),
                        usuario.getRol().getNombre()
                );

                // Devolver token y datos del usuario
                return new LoginResponseDTO(
                        token,
                        "Bearer",
                        convertirDTO(usuario)
                );
        }

        // Convertir modelo a DTO
        private UsuarioDTO convertirDTO(Usuario usuario) {

                RolDTO rolDTO = new RolDTO(
                        usuario.getRol().getId(),
                        usuario.getRol().getNombre()
                );

                return new UsuarioDTO(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getCorreo(),
                        usuario.getActivo(),
                        rolDTO
                );
        }
}
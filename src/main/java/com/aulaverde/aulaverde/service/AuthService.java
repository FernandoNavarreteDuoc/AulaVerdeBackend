package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.LoginDTO;
import com.aulaverde.aulaverde.DTO.RegistroDTO;
import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.DTO.RolDTO;

import com.aulaverde.aulaverde.model.Usuario;
import com.aulaverde.aulaverde.model.Rol;

import com.aulaverde.aulaverde.repository.UsuarioRepository;
import com.aulaverde.aulaverde.repository.RolRepository;

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

        // Encriptar contraseña
        usuario.setContrasena(
                passwordEncoder.encode(dto.getContrasena()));

        usuario.setActivo(true);
        usuario.setRol(rol);

        return convertirDTO(usuarioRepository.save(usuario));
    }

    // Iniciar sesión
    public UsuarioDTO login(LoginDTO dto) {

        String correo = dto.getCorreo().trim().toLowerCase();

        // Buscar usuario por correo
        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales incorrectas"));

        // Comprobar contraseña
        if (!passwordEncoder.matches(
                dto.getContrasena(),
                usuario.getContrasena())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas");
        }

        // Comprobar si el usuario está activo
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario está desactivado");
        }

        return convertirDTO(usuario);
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
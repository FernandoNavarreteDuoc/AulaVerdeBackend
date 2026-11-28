package com.aulaverde.aulaverde.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.ArrayList;
import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.DTO.RolDTO;
import com.aulaverde.aulaverde.model.Usuario;
import com.aulaverde.aulaverde.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los usuarios
    public List<UsuarioDTO> listar() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findAll()) {
            usuarios.add(convertirDTO(usuario));
        }
        return usuarios;
    }

    // Listar usuarios activos
    public List<UsuarioDTO> listarActivos() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findByActivoTrue()) {
            usuarios.add(convertirDTO(usuario));
        }
        return usuarios;
    }

    // Buscar usuario por ID
    public UsuarioDTO buscarPorId(Integer id) {
        return convertirDTO(obtenerUsuario(id));
    }

    // Listar usuarios por rol
    public List<UsuarioDTO> listarPorRol(String nombreRol) {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findByRolNombre(nombreRol)) {
            usuarios.add(convertirDTO(usuario));
        }
        return usuarios;
    }

    // Actualizar información personal
    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        Usuario usuario = obtenerUsuario(id);
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre es obligatorio");
        }
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El apellido es obligatorio");
        }
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        return convertirDTO(usuarioRepository.save(usuario));
    }

    // Desactivar usuario
    public void desactivar(Integer id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // Activar usuario
    public void activar(Integer id) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    // Obtener usuario desde la base de datos
    private Usuario obtenerUsuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Convertir modelo a DTO sin exponer la contraseña
    private UsuarioDTO convertirDTO(Usuario usuario) {
        RolDTO rolDTO = new RolDTO(usuario.getRol().getId(),
                usuario.getRol().getNombre());
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(),
                usuario.getApellido(), usuario.getCorreo(),
                usuario.getActivo(), rolDTO);
    }
}

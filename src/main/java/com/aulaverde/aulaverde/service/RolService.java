package com.aulaverde.aulaverde.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.ArrayList;
import com.aulaverde.aulaverde.DTO.RolDTO;
import com.aulaverde.aulaverde.model.Rol;
import com.aulaverde.aulaverde.repository.RolRepository;

@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    // Listar todos los roles
    public List<RolDTO> listar() {
        List<RolDTO> roles = new ArrayList<>();
        for (Rol rol : rolRepository.findAll()) {
            roles.add(convertirDTO(rol));
        }
        return roles;
    }

    // Buscar rol por ID
    public RolDTO buscarPorId(Integer id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rol no encontrado"));
        return convertirDTO(rol);
    }

    // Convertir modelo a DTO
    private RolDTO convertirDTO(Rol rol) {
        return new RolDTO(rol.getId(), rol.getNombre());
    }
}

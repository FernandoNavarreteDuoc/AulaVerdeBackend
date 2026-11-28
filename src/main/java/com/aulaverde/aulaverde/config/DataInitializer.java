package com.aulaverde.aulaverde.config;

import com.aulaverde.aulaverde.model.Rol;
import com.aulaverde.aulaverde.repository.RolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) {

        // Inicializar roles del sistema
        crearRol("DOCENTE");
        crearRol("INSTRUCTOR");
        crearRol("ESTUDIANTE");
    }

    // Crear rol si no existe
    private void crearRol(String nombre) {

        if (rolRepository.findByNombre(nombre).isEmpty()) {

            Rol rol = new Rol();

            rol.setNombre(nombre);

            rolRepository.save(rol);
        }
    }
}
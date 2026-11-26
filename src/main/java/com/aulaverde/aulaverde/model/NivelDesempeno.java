package com.aulaverde.aulaverde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "niveles_desempeno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelDesempeno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private Integer puntaje;

    @Column(nullable = false)
    private Integer orden;

    // Relaciones

    @ManyToOne
    @JoinColumn(name = "criterio_id", nullable = false)
    private Criterio criterio;
}
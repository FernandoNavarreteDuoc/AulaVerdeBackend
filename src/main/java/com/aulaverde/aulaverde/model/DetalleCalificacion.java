package com.aulaverde.aulaverde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalles_calificacion",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"calificacion_id", "criterio_id"}
       ))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer puntajeObtenido;

    @Column(length = 1000)
    private String observacion;

    // Relaciones

    @ManyToOne
    @JoinColumn(name = "calificacion_id", nullable = false)
    private Calificacion calificacion;

    @ManyToOne
    @JoinColumn(name = "criterio_id", nullable = false)
    private Criterio criterio;

    @ManyToOne
    @JoinColumn(name = "nivel_desempeno_id", nullable = false)
    private NivelDesempeno nivelDesempeno;
}
package com.proyecto.drones.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad planificada en la que participan uno o varios drones.
 *
 * @since 1.0
 */
public class Mision {
    /** Identificador único de la misión. */
    private String id;
    /** Nombre descriptivo. */
    private String nombre;
    /** Lugar de ejecución. */
    private String ubicacion;
    /** Fecha programada. */
    private LocalDate fecha;
    /** Drones asignados a la misión. */
    private List<Dron> drones = new ArrayList<>();

    /** @return identificador único de la misión */
    public String getId() { return id; }
    /** @param id identificador único de la misión */
    public void setId(String id) { this.id = id; }
    /** @return nombre descriptivo de la misión */
    public String getNombre() { return nombre; }
    /** @param nombre nombre descriptivo de la misión */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** @return lugar donde se realizará la misión */
    public String getUbicacion() { return ubicacion; }
    /** @param ubicacion lugar donde se realizará la misión */
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    /** @return fecha programada */
    public LocalDate getFecha() { return fecha; }
    /** @param fecha fecha programada */
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    /** @return lista mutable de drones asignados */
    public List<Dron> getDrones() { return drones; }
    /**
     * Reemplaza la asignación de drones por una copia de la lista.
     *
     * @param drones drones asignados; puede ser {@code null}
     */
    public void setDrones(List<Dron> drones) {
        this.drones = drones == null ? new ArrayList<>() : new ArrayList<>(drones);
    }
}

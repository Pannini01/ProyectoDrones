package com.proyecto.drones.dao;

import java.util.List;
import java.util.Optional;

import com.proyecto.drones.excepciones.PersistenciaException;

/**
 * Contrato CRUD reutilizable mediante parámetros genéricos.
 *
 * @param <T> tipo de entidad administrada
 * @param <ID> tipo de su identificador
 * @since 1.0
 */
public interface Crud<T, ID> {
    /**
     * Persiste una nueva entidad.
     *
     * @param entidad objeto que se guardará
     * @return misma entidad persistida
     * @throws PersistenciaException si la operación no puede completarse
     */
    T crear(T entidad) throws PersistenciaException;
    /**
     * Busca una entidad mediante su identificador.
     *
     * @param id identificador que se consultará
     * @return entidad encontrada o un {@link Optional} vacío
     * @throws PersistenciaException si la consulta no puede completarse
     */
    Optional<T> buscarPorId(ID id) throws PersistenciaException;
    /**
     * Recupera todas las entidades.
     *
     * @return lista, posiblemente vacía, de entidades
     * @throws PersistenciaException si la consulta no puede completarse
     */
    List<T> listar() throws PersistenciaException;
    /**
     * Reemplaza los datos persistidos de una entidad.
     *
     * @param entidad entidad con los nuevos datos
     * @return {@code true} si se actualizó un registro
     * @throws PersistenciaException si la operación no puede completarse
     */
    boolean actualizar(T entidad) throws PersistenciaException;
    /**
     * Elimina una entidad mediante su identificador.
     *
     * @param id identificador de la entidad
     * @return {@code true} si se eliminó un registro
     * @throws PersistenciaException si la operación no puede completarse
     */
    boolean eliminar(ID id) throws PersistenciaException;
}

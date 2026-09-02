package com.proyecto.drones.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.proyecto.drones.excepciones.PersistenciaException;
import com.proyecto.drones.modelo.Agricultura;
import com.proyecto.drones.modelo.Dron;
import com.proyecto.drones.modelo.TipoDron;
import com.proyecto.drones.modelo.Vigilancia;
import com.proyecto.drones.servicios.PostgresConnection;

/**
 * Implementación PostgreSQL del CRUD de drones.
 *
 * <p>Mapea la jerarquía {@link Dron} en una sola tabla mediante la columna
 * discriminadora {@code tipo}. Todos los recursos JDBC se cierran de forma
 * automática y los errores SQL se traducen a mensajes controlados.</p>
 *
 * @since 1.0
 */
public class DronDAO implements Crud<Dron, String> {
    /** Columnas comunes utilizadas para insertar y consultar drones. */
    private static final String COLUMNAS = "id, serial, modelo, fabricante, peso, tipo, "
            + "capacidad_tanque, deteccion_termica";
    /** Servicio Singleton que entrega conexiones JDBC. */
    private final PostgresConnection postgres;

    /**
     * Crea el DAO utilizando el servicio Singleton de PostgreSQL.
     */
    public DronDAO() {
        this(PostgresConnection.getInstance());
    }

    /**
     * Crea el DAO con un proveedor de conexiones explícito.
     *
     * @param postgres proveedor de conexiones; útil para pruebas del paquete
     */
    DronDAO(PostgresConnection postgres) {
        this.postgres = postgres;
    }

    /** {@inheritDoc} */
    @Override
    public Dron crear(Dron dron) throws PersistenciaException {
        String sql = "INSERT INTO dron (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, dron);
            sentencia.executeUpdate();
            return dron;
        } catch (SQLException e) {
            throw traducirError("guardar", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Dron> buscarPorId(String id) throws PersistenciaException {
        String sql = "SELECT " + COLUMNAS + " FROM dron WHERE id = ?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw traducirError("buscar", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<Dron> listar() throws PersistenciaException {
        String sql = "SELECT " + COLUMNAS + " FROM dron ORDER BY serial";
        List<Dron> drones = new ArrayList<>();
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                drones.add(mapear(resultado));
            }
            return drones;
        } catch (SQLException e) {
            throw traducirError("listar", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Dron dron) throws PersistenciaException {
        String sql = "UPDATE dron SET serial=?, modelo=?, fabricante=?, peso=?, tipo=?, "
                + "capacidad_tanque=?, deteccion_termica=? WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, dron.getSerial());
            sentencia.setString(2, dron.getModelo());
            sentencia.setString(3, dron.getFabricante());
            sentencia.setDouble(4, dron.getPeso());
            sentencia.setString(5, dron.getTipo().name());
            asignarCamposEspeciales(sentencia, 6, 7, dron);
            sentencia.setString(8, dron.getId());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("actualizar", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean eliminar(String id) throws PersistenciaException {
        String sql = "DELETE FROM dron WHERE id = ?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("eliminar", e);
        }
    }

    /**
     * Asigna los parámetros comunes y específicos de una inserción.
     *
     * @param sentencia sentencia preparada
     * @param dron entidad que se persistirá
     * @throws SQLException si JDBC rechaza algún parámetro
     */
    private void asignarParametros(PreparedStatement sentencia, Dron dron) throws SQLException {
        sentencia.setString(1, dron.getId());
        sentencia.setString(2, dron.getSerial());
        sentencia.setString(3, dron.getModelo());
        sentencia.setString(4, dron.getFabricante());
        sentencia.setDouble(5, dron.getPeso());
        sentencia.setString(6, dron.getTipo().name());
        asignarCamposEspeciales(sentencia, 7, 8, dron);
    }

    /**
     * Asigna capacidad de tanque o detección térmica según el subtipo.
     *
     * @param sentencia sentencia preparada
     * @param indiceTanque posición JDBC de la capacidad de tanque
     * @param indiceTermica posición JDBC de la detección térmica
     * @param dron entidad que aporta los datos
     * @throws SQLException si JDBC rechaza algún parámetro
     */
    private void asignarCamposEspeciales(PreparedStatement sentencia, int indiceTanque,
            int indiceTermica, Dron dron) throws SQLException {
        if (dron instanceof Agricultura agricultura) {
            sentencia.setDouble(indiceTanque, agricultura.getCapacidadTanque());
            sentencia.setNull(indiceTermica, Types.BOOLEAN);
        } else if (dron instanceof Vigilancia vigilancia) {
            sentencia.setNull(indiceTanque, Types.DOUBLE);
            sentencia.setBoolean(indiceTermica, vigilancia.isDeteccionTermica());
        }
    }

    /**
     * Reconstruye el subtipo correcto a partir de una fila SQL.
     *
     * @param resultado fila posicionada del resultado
     * @return dron agrícola o de vigilancia
     * @throws SQLException si no puede leerse una columna
     * @throws PersistenciaException si el discriminador es desconocido
     */
    private Dron mapear(ResultSet resultado) throws SQLException, PersistenciaException {
        TipoDron tipo;
        try {
            tipo = TipoDron.valueOf(resultado.getString("tipo"));
        } catch (IllegalArgumentException e) {
            throw new PersistenciaException("La base de datos contiene un tipo de dron desconocido.", e);
        }
        if (tipo == TipoDron.AGRICULTURA) {
            return new Agricultura(resultado.getString("id"), resultado.getString("serial"),
                    resultado.getString("modelo"), resultado.getString("fabricante"),
                    resultado.getDouble("peso"), resultado.getDouble("capacidad_tanque"));
        }
        return new Vigilancia(resultado.getString("id"), resultado.getString("serial"),
                resultado.getString("modelo"), resultado.getString("fabricante"),
                resultado.getDouble("peso"), resultado.getBoolean("deteccion_termica"));
    }

    /**
     * Convierte códigos SQL conocidos en mensajes comprensibles.
     *
     * @param operacion verbo que describe la operación ejecutada
     * @param e excepción JDBC original
     * @return excepción controlada que conserva la causa
     */
    private PersistenciaException traducirError(String operacion, SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return new PersistenciaException("Ya existe un dron con ese ID o serial.", e);
        }
        if ("42P01".equals(e.getSQLState())) {
            return new PersistenciaException("No existe la tabla dron. Ejecute database/schema.sql.", e);
        }
        return new PersistenciaException("No fue posible " + operacion + " el dron en PostgreSQL.", e);
    }
}

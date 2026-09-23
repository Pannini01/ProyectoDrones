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
import com.proyecto.drones.servicios.ModoControl;
import com.proyecto.drones.servicios.PostgresConnection;

/**
 * Implementación PostgreSQL del CRUD de drones.
 *
 * <p>Mapea la jerarquía {@link Dron} en una sola tabla mediante la columna
 * discriminadora {@code tipo}. La batería del Decorator y el modo del Bridge
 * se persisten como estado de infraestructura, sin agregar atributos al
 * package {@code modelo}. Todos los recursos JDBC se cierran de forma
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

    /** Crea el DAO utilizando el servicio Singleton de PostgreSQL. */
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

    /**
     * Persiste un dron junto con la selección del Decorator. Mantiene el modo
     * Bridge en MANUAL para compatibilidad con llamadas anteriores.
     *
     * @param dron entidad que se persistirá
     * @param bateriaAdicional {@code true} si el dron lleva batería adicional
     * @return entidad persistida
     * @throws PersistenciaException si PostgreSQL rechaza la operación
     */
    public Dron crear(Dron dron, boolean bateriaAdicional) throws PersistenciaException {
        return crear(dron, bateriaAdicional, ModoControl.CODIGO_MANUAL);
    }

    /**
     * Persiste un dron con el estado del Decorator y el modo Bridge.
     *
     * @param dron entidad que se persistirá
     * @param bateriaAdicional estado de la batería adicional
     * @param modoControl código MANUAL o AUTONOMO
     * @return entidad persistida
     * @throws PersistenciaException si PostgreSQL rechaza la operación
     */
    public Dron crear(Dron dron, boolean bateriaAdicional, String modoControl)
            throws PersistenciaException {
        validarModoControl(modoControl);
        String sql = "INSERT INTO dron (" + COLUMNAS
                + ", bateria_adicional, modo_control) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, dron);
            sentencia.setBoolean(9, bateriaAdicional);
            sentencia.setString(10, modoControl);
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

    /**
     * Actualiza el dron y la selección de batería adicional sin cambiar el modo
     * Bridge persistido.
     *
     * @param dron entidad actualizada
     * @param bateriaAdicional estado seleccionado en la interfaz
     * @return {@code true} si existía el registro
     * @throws PersistenciaException si ocurre un error JDBC
     */
    public boolean actualizar(Dron dron, boolean bateriaAdicional) throws PersistenciaException {
        String sql = "UPDATE dron SET serial=?, modelo=?, fabricante=?, peso=?, tipo=?, "
                + "capacidad_tanque=?, deteccion_termica=?, bateria_adicional=? WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, dron.getSerial());
            sentencia.setString(2, dron.getModelo());
            sentencia.setString(3, dron.getFabricante());
            sentencia.setDouble(4, dron.getPeso());
            sentencia.setString(5, dron.getTipo().name());
            asignarCamposEspeciales(sentencia, 6, 7, dron);
            sentencia.setBoolean(8, bateriaAdicional);
            sentencia.setString(9, dron.getId());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("actualizar", e);
        }
    }

    /**
     * Actualiza en una sola sentencia los datos del dron, el Decorator y Bridge.
     *
     * @param dron entidad actualizada
     * @param bateriaAdicional estado de batería adicional
     * @param modoControl código MANUAL o AUTONOMO
     * @return {@code true} si existía el registro
     * @throws PersistenciaException si ocurre un error JDBC
     */
    public boolean actualizar(Dron dron, boolean bateriaAdicional, String modoControl)
            throws PersistenciaException {
        validarModoControl(modoControl);
        String sql = "UPDATE dron SET serial=?, modelo=?, fabricante=?, peso=?, tipo=?, "
                + "capacidad_tanque=?, deteccion_termica=?, bateria_adicional=?, modo_control=? WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, dron.getSerial());
            sentencia.setString(2, dron.getModelo());
            sentencia.setString(3, dron.getFabricante());
            sentencia.setDouble(4, dron.getPeso());
            sentencia.setString(5, dron.getTipo().name());
            asignarCamposEspeciales(sentencia, 6, 7, dron);
            sentencia.setBoolean(8, bateriaAdicional);
            sentencia.setString(9, modoControl);
            sentencia.setString(10, dron.getId());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("actualizar", e);
        }
    }

    /**
     * Guarda únicamente la selección de batería adicional de un dron ya persistido.
     *
     * @param id identificador del dron
     * @param bateriaAdicional nuevo estado de la selección
     * @return {@code true} si el dron existía
     * @throws PersistenciaException si ocurre un error JDBC
     */
    public boolean actualizarBateriaAdicional(String id, boolean bateriaAdicional)
            throws PersistenciaException {
        String sql = "UPDATE dron SET bateria_adicional=? WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setBoolean(1, bateriaAdicional);
            sentencia.setString(2, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("actualizar la bateria adicional de", e);
        }
    }

    /**
     * Consulta la selección persistida de batería adicional.
     *
     * @param id identificador del dron
     * @return {@code true} si el dron tiene batería adicional
     * @throws PersistenciaException si ocurre un error JDBC
     */
    public boolean tieneBateriaAdicional(String id) throws PersistenciaException {
        String sql = "SELECT bateria_adicional FROM dron WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getBoolean("bateria_adicional");
            }
        } catch (SQLException e) {
            throw traducirError("consultar la bateria adicional de", e);
        }
    }

    /**
     * Persiste inmediatamente el modo Bridge de un dron existente.
     *
     * @param id identificador del dron
     * @param modoControl código MANUAL o AUTONOMO
     * @return {@code true} si el dron existía
     * @throws PersistenciaException si ocurre un error JDBC
     */
    public boolean actualizarModoControl(String id, String modoControl) throws PersistenciaException {
        validarModoControl(modoControl);
        String sql = "UPDATE dron SET modo_control=? WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, modoControl);
            sentencia.setString(2, id);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducirError("actualizar el modo de control de", e);
        }
    }

    /**
     * Consulta el modo Bridge persistido. Un ID todavía no almacenado utiliza
     * MANUAL como valor inicial, igual que el DEFAULT de PostgreSQL.
     *
     * @param id identificador del dron
     * @return código MANUAL o AUTONOMO
     * @throws PersistenciaException si ocurre un error JDBC o hay un valor inválido
     */
    public String obtenerModoControl(String id) throws PersistenciaException {
        String sql = "SELECT modo_control FROM dron WHERE id=?";
        try (Connection conexion = postgres.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return ModoControl.CODIGO_MANUAL;
                }
                String modo = resultado.getString("modo_control");
                validarModoControl(modo);
                return modo;
            }
        } catch (SQLException e) {
            throw traducirError("consultar el modo de control de", e);
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

    /** Asigna los parámetros comunes y específicos de una inserción. */
    private void asignarParametros(PreparedStatement sentencia, Dron dron) throws SQLException {
        sentencia.setString(1, dron.getId());
        sentencia.setString(2, dron.getSerial());
        sentencia.setString(3, dron.getModelo());
        sentencia.setString(4, dron.getFabricante());
        sentencia.setDouble(5, dron.getPeso());
        sentencia.setString(6, dron.getTipo().name());
        asignarCamposEspeciales(sentencia, 7, 8, dron);
    }

    /** Asigna capacidad de tanque o detección térmica según el subtipo. */
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

    /** Reconstruye el subtipo correcto a partir de una fila SQL. */
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

    /** Valida los dos únicos códigos admitidos por las subclases Bridge. */
    private void validarModoControl(String modoControl) throws PersistenciaException {
        if (!ModoControl.CODIGO_MANUAL.equals(modoControl)
                && !ModoControl.CODIGO_AUTONOMO.equals(modoControl)) {
            throw new PersistenciaException(
                    "El modo de control debe ser MANUAL o AUTONOMO.");
        }
    }

    /** Convierte códigos SQL conocidos en mensajes comprensibles. */
    private PersistenciaException traducirError(String operacion, SQLException e) {
        if ("23505".equals(e.getSQLState())) {
            return new PersistenciaException("Ya existe un dron con ese ID o serial.", e);
        }
        if ("23514".equals(e.getSQLState())) {
            return new PersistenciaException(
                    "PostgreSQL rechazo un valor por una restriccion CHECK del dron.", e);
        }
        if ("42P01".equals(e.getSQLState())) {
            return new PersistenciaException("No existe la tabla dron. Ejecute database/schema.sql.", e);
        }
        if ("42703".equals(e.getSQLState())) {
            return new PersistenciaException(
                    "La base de datos no tiene todas las columnas requeridas. Ejecute "
                    + "database/actualizar_modo_control_bridge.sql y, si corresponde, database/schema.sql.", e);
        }
        return new PersistenciaException("No fue posible " + operacion + " el dron en PostgreSQL.", e);
    }
}

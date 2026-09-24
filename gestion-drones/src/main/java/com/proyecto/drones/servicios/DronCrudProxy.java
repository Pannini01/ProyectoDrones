package com.proyecto.drones.servicios;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.proyecto.drones.dao.Crud;
import com.proyecto.drones.dao.DronDAO;
import com.proyecto.drones.excepciones.PersistenciaException;
import com.proyecto.drones.excepciones.ValidacionException;
import com.proyecto.drones.modelo.Dron;

/**
 * Proxy de proteccion para el CRUD de drones.
 *
 * <p>Las operaciones de creacion, consulta y actualizacion se delegan al
 * {@link DronDAO} real. La eliminacion directa queda bloqueada y solamente se
 * permite mediante {@link #eliminarProtegido(String, String)}, que valida la
 * contrasena configurada en {@code DELETE_PASSWORD} dentro del archivo
 * {@code .env}. De esta forma el Proxy controla el acceso sin modificar las
 * entidades del package {@code modelo}.</p>
 *
 * @since 1.3
 */
public final class DronCrudProxy implements Crud<Dron, String> {

    /** Objeto real al que se delega el acceso a PostgreSQL. */
    private final DronDAO real;
    /** Fuente de la contrasena esperada; permite probar el Proxy sin PostgreSQL. */
    private final ProveedorContrasenaEliminacion proveedorContrasena;

    /**
     * Crea el Proxy usando un DAO real y la configuracion del archivo .env.
     */
    public DronCrudProxy() {
        this(new DronDAO());
    }

    /**
     * Crea el Proxy sobre el DAO indicado.
     *
     * @param real DAO real que ejecuta las operaciones de persistencia
     */
    public DronCrudProxy(DronDAO real) {
        this(real, DronCrudProxy::leerContrasenaConfigurada);
    }

    /**
     * Crea el Proxy con un proveedor de contrasena inyectable.
     *
     * <p>Este constructor es util para pruebas JUnit porque permite comprobar
     * la autorizacion sin depender de un archivo .env real.</p>
     *
     * @param real DAO real que sera protegido
     * @param proveedorContrasena fuente de la contrasena esperada
     */
    public DronCrudProxy(DronDAO real, ProveedorContrasenaEliminacion proveedorContrasena) {
        this.real = Objects.requireNonNull(real, "El DAO real no puede ser nulo.");
        this.proveedorContrasena = Objects.requireNonNull(
                proveedorContrasena,
                "El proveedor de contrasena no puede ser nulo.");
    }

    /** {@inheritDoc} */
    @Override
    public Dron crear(Dron entidad) throws PersistenciaException {
        return real.crear(entidad);
    }

    /**
     * Delega la creacion completa usada por la aplicacion actual.
     *
     * @param dron dron que se persistira
     * @param bateriaAdicional estado del Decorator
     * @param modoControl codigo del Bridge
     * @return entidad persistida
     * @throws PersistenciaException si falla PostgreSQL
     */
    public Dron crear(Dron dron, boolean bateriaAdicional, String modoControl)
            throws PersistenciaException {
        return real.crear(dron, bateriaAdicional, modoControl);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Dron> buscarPorId(String id) throws PersistenciaException {
        return real.buscarPorId(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Dron> listar() throws PersistenciaException {
        return real.listar();
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizar(Dron entidad) throws PersistenciaException {
        return real.actualizar(entidad);
    }

    /**
     * Delega la actualizacion completa usada por la interfaz JavaFX.
     *
     * @param dron dron actualizado
     * @param bateriaAdicional estado del Decorator
     * @param modoControl codigo del Bridge
     * @return true si existia el registro
     * @throws PersistenciaException si falla PostgreSQL
     */
    public boolean actualizar(Dron dron, boolean bateriaAdicional, String modoControl)
            throws PersistenciaException {
        return real.actualizar(dron, bateriaAdicional, modoControl);
    }

    /**
     * Bloquea la eliminacion directa para evitar que el Proxy sea omitido.
     *
     * @param id identificador del dron
     * @return nunca retorna normalmente
     * @throws PersistenciaException siempre, indicando que debe usarse la
     *         operacion protegida
     */
    @Override
    public boolean eliminar(String id) throws PersistenciaException {
        throw new PersistenciaException(
                "La eliminacion directa esta protegida por Proxy. Use eliminarProtegido con contrasena.");
    }

    /**
     * Valida la contrasena y, solo si es correcta, delega la eliminacion al DAO.
     *
     * @param id identificador del dron
     * @param contrasena contrasena digitada por el usuario
     * @return true si PostgreSQL elimino el registro
     * @throws PersistenciaException si falla la configuracion o PostgreSQL
     * @throws ValidacionException si la contrasena esta vacia o es incorrecta
     */
    public boolean eliminarProtegido(String id, String contrasena)
            throws PersistenciaException, ValidacionException {
        if (contrasena == null || contrasena.isBlank()) {
            throw new ValidacionException("Debe ingresar la contrasena para eliminar el dron.");
        }
        String esperada = proveedorContrasena.obtener();
        if (!coincide(contrasena, esperada)) {
            throw new ValidacionException("Contrasena incorrecta. El dron no fue eliminado.");
        }
        return real.eliminar(id);
    }

    /**
     * Delega el cambio inmediato de bateria adicional.
     *
     * @param id identificador del dron
     * @param bateriaAdicional nuevo estado del Decorator
     * @return true si existia el registro
     * @throws PersistenciaException si falla PostgreSQL
     */
    public boolean actualizarBateriaAdicional(String id, boolean bateriaAdicional)
            throws PersistenciaException {
        return real.actualizarBateriaAdicional(id, bateriaAdicional);
    }

    /**
     * Delega la consulta del estado de bateria adicional.
     *
     * @param id identificador del dron
     * @return true si la bateria adicional esta persistida
     * @throws PersistenciaException si falla PostgreSQL
     */
    public boolean tieneBateriaAdicional(String id) throws PersistenciaException {
        return real.tieneBateriaAdicional(id);
    }

    /**
     * Delega el cambio inmediato del modo Bridge.
     *
     * @param id identificador del dron
     * @param modoControl codigo MANUAL o AUTONOMO
     * @return true si existia el registro
     * @throws PersistenciaException si falla PostgreSQL
     */
    public boolean actualizarModoControl(String id, String modoControl)
            throws PersistenciaException {
        return real.actualizarModoControl(id, modoControl);
    }

    /**
     * Delega la consulta del modo Bridge persistido.
     *
     * @param id identificador del dron
     * @return codigo MANUAL o AUTONOMO
     * @throws PersistenciaException si falla PostgreSQL
     */
    public String obtenerModoControl(String id) throws PersistenciaException {
        return real.obtenerModoControl(id);
    }

    /** Lee la contrasena de eliminacion desde el archivo .env. */
    private static String leerContrasenaConfigurada() throws PersistenciaException {
        return ConfiguracionEnv.cargar().requerido("DELETE_PASSWORD");
    }

    /** Compara las contrasenas sin utilizar equals de forma directa. */
    private static boolean coincide(String recibida, String esperada) {
        byte[] a = recibida.getBytes(StandardCharsets.UTF_8);
        byte[] b = esperada.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    /**
     * Fuente de la contrasena esperada por el Proxy.
     */
    @FunctionalInterface
    public interface ProveedorContrasenaEliminacion {
        /**
         * Obtiene la contrasena esperada por el Proxy.
         *
         * @return contrasena configurada para autorizar eliminaciones
         * @throws PersistenciaException si no puede obtenerse la configuracion
         */
        String obtener() throws PersistenciaException;
    }
}

package com.proyecto.drones.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.proyecto.drones.excepciones.PersistenciaException;

/**
 * Singleton que centraliza la configuración y creación de conexiones
 * PostgreSQL.
 *
 * <p>La única instancia no mantiene una conexión global abierta. Cada llamada
 * a {@link #getConnection()} crea una conexión que el DAO debe cerrar mediante
 * {@code try-with-resources}. Esta estrategia evita reutilizar conexiones
 * inválidas.</p>
 *
 * @since 1.0
 */
public final class PostgresConnection {
    /** Única instancia creada durante la carga de la clase. */
    private static final PostgresConnection INSTANCIA = new PostgresConnection();

    /** Impide crear instancias fuera de la clase. */
    private PostgresConnection() {
    }

    /**
     * Obtiene la única instancia del servicio.
     *
     * @return instancia Singleton
     */
    public static PostgresConnection getInstance() {
        return INSTANCIA;
    }

    /**
     * Abre una conexión usando las variables del archivo {@code .env}.
     *
     * @return conexión JDBC abierta
     * @throws PersistenciaException si falta configuración o PostgreSQL no
     *         acepta la conexión
     */
    public Connection getConnection() throws PersistenciaException {
        ConfiguracionEnv env = ConfiguracionEnv.cargar();
        String url = env.requerido("DB_URL");
        String usuario = env.requerido("DB_USER");
        String clave = env.requerido("DB_PASSWORD");
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No fue posible conectar con PostgreSQL. Verifique .env y que la base de datos este activa.", e);
        }
    }
}

package com.proyecto.drones.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.proyecto.drones.dao.DronDAO;
import com.proyecto.drones.excepciones.PersistenciaException;
import com.proyecto.drones.excepciones.ValidacionException;

/** Pruebas JUnit del patron Proxy aplicado al CRUD de drones. */
class ProxyTest {

    /** La contrasena correcta permite delegar la eliminacion al DAO real. */
    @Test
    void proxyPermiteEliminarConContrasenaCorrecta() throws Exception {
        DronDaoFalso real = new DronDaoFalso();
        DronCrudProxy proxy = new DronCrudProxy(real, () -> "clave-segura");

        assertTrue(proxy.eliminarProtegido("DRON-1", "clave-segura"));
        assertEquals(1, real.eliminaciones);
    }

    /** Una contrasena incorrecta bloquea la llamada al DAO real. */
    @Test
    void proxyBloqueaEliminarConContrasenaIncorrecta() {
        DronDaoFalso real = new DronDaoFalso();
        DronCrudProxy proxy = new DronCrudProxy(real, () -> "clave-segura");

        assertThrows(ValidacionException.class,
                () -> proxy.eliminarProtegido("DRON-1", "incorrecta"));
        assertEquals(0, real.eliminaciones);
    }

    /** La eliminacion sin pasar por la autorizacion queda bloqueada. */
    @Test
    void proxyBloqueaEliminacionDirecta() {
        DronDaoFalso real = new DronDaoFalso();
        DronCrudProxy proxy = new DronCrudProxy(real, () -> "clave-segura");

        assertThrows(PersistenciaException.class, () -> proxy.eliminar("DRON-1"));
        assertFalse(real.eliminaciones > 0);
    }

    /** DAO falso que evita depender de PostgreSQL durante JUnit. */
    private static final class DronDaoFalso extends DronDAO {
        private int eliminaciones;

        @Override
        public boolean eliminar(String id) {
            eliminaciones++;
            return true;
        }
    }
}

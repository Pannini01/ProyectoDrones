-- ============================================================
-- MIGRACION ACTIVIDAD - COMPOSITE Y ADAPTER
-- PostgreSQL / pgAdmin 4
-- Ejecutar conectado a drones_db.
--
-- Composite: agrega una tabla autorreferenciada y la estructura requerida.
-- Adapter: no requiere tabla nueva porque su salida es un archivo .json.
-- ============================================================

BEGIN;

CREATE TABLE IF NOT EXISTS sensor_composite (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_nodo VARCHAR(15) NOT NULL
        CHECK (tipo_nodo IN ('COMPUESTO', 'HOJA')),
    padre_id VARCHAR(36),
    orden SMALLINT NOT NULL DEFAULT 0 CHECK (orden >= 0),

    CONSTRAINT fk_sensor_composite_padre
        FOREIGN KEY (padre_id)
        REFERENCES sensor_composite(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT uq_sensor_composite_padre_nombre
        UNIQUE (padre_id, nombre)
);

CREATE INDEX IF NOT EXISTS idx_sensor_composite_padre_id
    ON sensor_composite(padre_id);

INSERT INTO sensor_composite (id, nombre, tipo_nodo, padre_id, orden) VALUES
    ('SC-GENERAL', 'Sensor General', 'COMPUESTO', NULL, 1),
    ('SC-TEMP', 'Sensor Temperatura', 'COMPUESTO', 'SC-GENERAL', 1),
    ('SC-CAMARA', 'Sensor Cámara', 'COMPUESTO', 'SC-GENERAL', 2),
    ('SC-SONIDO', 'Sensor Sonido', 'COMPUESTO', 'SC-GENERAL', 3),
    ('SC-INTELIGENTE', 'Sensor Inteligente', 'HOJA', 'SC-GENERAL', 4),
    ('SC-TEMP-IR', 'Sensor Infrarrojo', 'HOJA', 'SC-TEMP', 1),
    ('SC-TEMP-RTD', 'RTD', 'HOJA', 'SC-TEMP', 2),
    ('SC-CAM-CMOS', 'Sensor CMOS', 'HOJA', 'SC-CAMARA', 1),
    ('SC-CAM-CCD', 'Sensor CCD', 'HOJA', 'SC-CAMARA', 2),
    ('SC-SON-ANA', 'Sensor Analógico', 'HOJA', 'SC-SONIDO', 1),
    ('SC-SON-DIG', 'Sensor Digital', 'COMPUESTO', 'SC-SONIDO', 2),
    ('SC-DIG-SPI', 'SPI', 'HOJA', 'SC-SON-DIG', 1),
    ('SC-DIG-UART', 'UART', 'HOJA', 'SC-SON-DIG', 2)
ON CONFLICT (id) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    tipo_nodo = EXCLUDED.tipo_nodo,
    padre_id = EXCLUDED.padre_id,
    orden = EXCLUDED.orden;

COMMENT ON TABLE sensor_composite IS
    'Jerarquia de sensores usada como evidencia persistida del patron Composite.';

COMMIT;

-- Verificacion rapida: deben aparecer 13 nodos.
SELECT id, nombre, tipo_nodo, padre_id, orden
FROM sensor_composite
ORDER BY COALESCE(padre_id, ''), orden, nombre;

-- Verificacion de la llave foranea autorreferenciada.
SELECT
    tc.constraint_name,
    kcu.column_name,
    ccu.table_name AS tabla_referenciada,
    ccu.column_name AS columna_referenciada
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
 AND tc.constraint_schema = kcu.constraint_schema
JOIN information_schema.constraint_column_usage ccu
  ON tc.constraint_name = ccu.constraint_name
 AND tc.constraint_schema = ccu.constraint_schema
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'sensor_composite'
  AND tc.constraint_type = 'FOREIGN KEY';

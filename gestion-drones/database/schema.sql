-- ============================================================
-- ESQUEMA COMPLETO - SISTEMA DE GESTION DE DRONES
-- PostgreSQL / pgAdmin 4
-- Ejecutar conectado a la base drones_db.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1. PILOTO
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS piloto (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    licencia VARCHAR(80) NOT NULL UNIQUE,
    telefono VARCHAR(30)
);

-- ------------------------------------------------------------
-- 2. DRON
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dron (
    id VARCHAR(36) PRIMARY KEY,
    serial VARCHAR(80) NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    peso NUMERIC(10, 2) NOT NULL CHECK (peso > 0),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('AGRICULTURA', 'VIGILANCIA')),
    capacidad_tanque NUMERIC(10, 2),
    deteccion_termica BOOLEAN,
    bateria_adicional BOOLEAN NOT NULL DEFAULT FALSE,
    modo_control VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    piloto_id VARCHAR(36),

    CONSTRAINT ck_dron_modo_control CHECK (
        modo_control IN ('MANUAL', 'AUTONOMO')
    ),

    CONSTRAINT ck_dron_subtipo CHECK (
        (tipo = 'AGRICULTURA'
            AND capacidad_tanque IS NOT NULL
            AND capacidad_tanque > 0
            AND deteccion_termica IS NULL)
        OR
        (tipo = 'VIGILANCIA'
            AND capacidad_tanque IS NULL
            AND deteccion_termica IS NOT NULL)
    ),

    CONSTRAINT fk_dron_piloto
        FOREIGN KEY (piloto_id)
        REFERENCES piloto(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Compatibilidad con versiones anteriores del proyecto.
ALTER TABLE dron
    ADD COLUMN IF NOT EXISTS bateria_adicional BOOLEAN;

UPDATE dron
SET bateria_adicional = FALSE
WHERE bateria_adicional IS NULL;

ALTER TABLE dron
    ALTER COLUMN bateria_adicional SET DEFAULT FALSE;

ALTER TABLE dron
    ALTER COLUMN bateria_adicional SET NOT NULL;

-- Compatibilidad con versiones anteriores: persistencia del modo Bridge.
ALTER TABLE dron
    ADD COLUMN IF NOT EXISTS modo_control VARCHAR(20);

UPDATE dron
SET modo_control = 'MANUAL'
WHERE modo_control IS NULL
   OR modo_control NOT IN ('MANUAL', 'AUTONOMO');

ALTER TABLE dron
    ALTER COLUMN modo_control SET DEFAULT 'MANUAL';

ALTER TABLE dron
    ALTER COLUMN modo_control SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON t.oid = c.conrelid
        JOIN pg_namespace n ON n.oid = t.relnamespace
        WHERE c.conname = 'ck_dron_modo_control'
          AND t.relname = 'dron'
          AND n.nspname = 'public'
    ) THEN
        ALTER TABLE public.dron
            ADD CONSTRAINT ck_dron_modo_control
            CHECK (modo_control IN ('MANUAL', 'AUTONOMO'));
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 3. SENSOR
-- Cada sensor pertenece a un dron.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sensor (
    id VARCHAR(36) PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    dron_id VARCHAR(36) NOT NULL,

    CONSTRAINT fk_sensor_dron
        FOREIGN KEY (dron_id)
        REFERENCES dron(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ------------------------------------------------------------
-- 4. MISION
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mision (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    ubicacion VARCHAR(180) NOT NULL,
    fecha DATE NOT NULL
);

-- ------------------------------------------------------------
-- 5. MISION_DRON
-- Tabla intermedia para la relacion muchos-a-muchos.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mision_dron (
    mision_id VARCHAR(36) NOT NULL,
    dron_id VARCHAR(36) NOT NULL,

    CONSTRAINT pk_mision_dron
        PRIMARY KEY (mision_id, dron_id),

    CONSTRAINT fk_mision_dron_mision
        FOREIGN KEY (mision_id)
        REFERENCES mision(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_mision_dron_dron
        FOREIGN KEY (dron_id)
        REFERENCES dron(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- ------------------------------------------------------------
-- 6. SENSOR_COMPOSITE
-- Jerarquia persistida como evidencia del patron Composite.
-- La logica del patron permanece en el package servicios.
-- ------------------------------------------------------------
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

-- Estructura requerida por la actividad.
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

-- ------------------------------------------------------------
-- 7. INDICES
-- PostgreSQL no crea automaticamente indices para las columnas FK.
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_dron_tipo
    ON dron(tipo);

CREATE INDEX IF NOT EXISTS idx_dron_piloto_id
    ON dron(piloto_id);

CREATE INDEX IF NOT EXISTS idx_sensor_dron_id
    ON sensor(dron_id);

CREATE INDEX IF NOT EXISTS idx_mision_dron_dron_id
    ON mision_dron(dron_id);

CREATE INDEX IF NOT EXISTS idx_sensor_composite_padre_id
    ON sensor_composite(padre_id);

COMMENT ON COLUMN dron.bateria_adicional IS
    'Indica si el dron tiene aplicada la opcion BateriaAdicionalDecorator.';

COMMENT ON COLUMN dron.modo_control IS
    'Modo Bridge persistido para el dron: MANUAL o AUTONOMO.';

COMMENT ON TABLE sensor_composite IS
    'Jerarquia de sensores usada como evidencia persistida del patron Composite.';

COMMIT;

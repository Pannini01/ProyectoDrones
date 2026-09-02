-- Ejecute este script conectado a la base drones_db.
CREATE TABLE IF NOT EXISTS piloto (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    licencia VARCHAR(80) NOT NULL UNIQUE,
    telefono VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS dron (
    id VARCHAR(36) PRIMARY KEY,
    serial VARCHAR(80) NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    peso NUMERIC(10, 2) NOT NULL CHECK (peso > 0),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('AGRICULTURA', 'VIGILANCIA')),
    capacidad_tanque NUMERIC(10, 2),
    deteccion_termica BOOLEAN,
    piloto_id VARCHAR(36) REFERENCES piloto(id) ON DELETE SET NULL,
    CONSTRAINT ck_dron_subtipo CHECK (
        (tipo = 'AGRICULTURA' AND capacidad_tanque IS NOT NULL
            AND capacidad_tanque > 0 AND deteccion_termica IS NULL)
        OR
        (tipo = 'VIGILANCIA' AND capacidad_tanque IS NULL
            AND deteccion_termica IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_dron_tipo ON dron(tipo);

CREATE TABLE IF NOT EXISTS sensor (
    id VARCHAR(36) PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    dron_id VARCHAR(36) NOT NULL REFERENCES dron(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mision (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    ubicacion VARCHAR(180) NOT NULL,
    fecha DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS mision_dron (
    mision_id VARCHAR(36) NOT NULL REFERENCES mision(id) ON DELETE CASCADE,
    dron_id VARCHAR(36) NOT NULL REFERENCES dron(id) ON DELETE CASCADE,
    PRIMARY KEY (mision_id, dron_id)
);

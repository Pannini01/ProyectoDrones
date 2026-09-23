-- ============================================================
-- MIGRACION SEGURA DE LLAVES FORANEAS
-- PostgreSQL / pgAdmin 4
--
-- Ejecutar conectado a drones_db.
-- El script NO elimina datos. Si detecta registros huerfanos,
-- detiene la migracion para que sean corregidos conscientemente.
-- Es idempotente: puede ejecutarse mas de una vez.
-- ============================================================

BEGIN;

-- ------------------------------------------------------------
-- 1. Validar que existan las tablas esperadas
-- ------------------------------------------------------------
DO $$
DECLARE
    tabla TEXT;
BEGIN
    FOREACH tabla IN ARRAY ARRAY['piloto', 'dron', 'sensor', 'mision', 'mision_dron']
    LOOP
        IF to_regclass('public.' || tabla) IS NULL THEN
            RAISE EXCEPTION 'No existe la tabla public.%. Ejecute primero database/schema.sql.', tabla;
        END IF;
    END LOOP;
END
$$;

-- ------------------------------------------------------------
-- 2. Mantener la columna del Decorator
-- ------------------------------------------------------------
ALTER TABLE public.dron
    ADD COLUMN IF NOT EXISTS bateria_adicional BOOLEAN;

UPDATE public.dron
SET bateria_adicional = FALSE
WHERE bateria_adicional IS NULL;

ALTER TABLE public.dron
    ALTER COLUMN bateria_adicional SET DEFAULT FALSE;

ALTER TABLE public.dron
    ALTER COLUMN bateria_adicional SET NOT NULL;

-- ------------------------------------------------------------
-- 3. Comprobar integridad ANTES de agregar llaves foraneas
-- ------------------------------------------------------------
DO $$
DECLARE
    cantidad BIGINT;
BEGIN
    SELECT COUNT(*) INTO cantidad
    FROM public.dron d
    LEFT JOIN public.piloto p ON p.id = d.piloto_id
    WHERE d.piloto_id IS NOT NULL
      AND p.id IS NULL;

    IF cantidad > 0 THEN
        RAISE EXCEPTION
            'No se puede crear FK dron->piloto: existen % registros de dron con piloto_id inexistente.',
            cantidad;
    END IF;

    SELECT COUNT(*) INTO cantidad
    FROM public.sensor s
    LEFT JOIN public.dron d ON d.id = s.dron_id
    WHERE d.id IS NULL;

    IF cantidad > 0 THEN
        RAISE EXCEPTION
            'No se puede crear FK sensor->dron: existen % sensores huerfanos.', cantidad;
    END IF;

    SELECT COUNT(*) INTO cantidad
    FROM public.mision_dron md
    LEFT JOIN public.mision m ON m.id = md.mision_id
    WHERE m.id IS NULL;

    IF cantidad > 0 THEN
        RAISE EXCEPTION
            'No se puede crear FK mision_dron->mision: existen % asociaciones huerfanas.', cantidad;
    END IF;

    SELECT COUNT(*) INTO cantidad
    FROM public.mision_dron md
    LEFT JOIN public.dron d ON d.id = md.dron_id
    WHERE d.id IS NULL;

    IF cantidad > 0 THEN
        RAISE EXCEPTION
            'No se puede crear FK mision_dron->dron: existen % asociaciones huerfanas.', cantidad;
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 4. DRON -> PILOTO
-- ON DELETE SET NULL: eliminar el piloto no elimina el dron.
-- ------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.constraint_schema = kcu.constraint_schema
        JOIN information_schema.constraint_column_usage ccu
          ON tc.constraint_name = ccu.constraint_name
         AND tc.constraint_schema = ccu.constraint_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_schema = 'public'
          AND tc.table_name = 'dron'
          AND kcu.column_name = 'piloto_id'
          AND ccu.table_schema = 'public'
          AND ccu.table_name = 'piloto'
          AND ccu.column_name = 'id'
    ) THEN
        ALTER TABLE public.dron
            ADD CONSTRAINT fk_dron_piloto
            FOREIGN KEY (piloto_id)
            REFERENCES public.piloto(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 5. SENSOR -> DRON
-- ON DELETE CASCADE: un sensor no tiene sentido sin su dron.
-- ------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.constraint_schema = kcu.constraint_schema
        JOIN information_schema.constraint_column_usage ccu
          ON tc.constraint_name = ccu.constraint_name
         AND tc.constraint_schema = ccu.constraint_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_schema = 'public'
          AND tc.table_name = 'sensor'
          AND kcu.column_name = 'dron_id'
          AND ccu.table_schema = 'public'
          AND ccu.table_name = 'dron'
          AND ccu.column_name = 'id'
    ) THEN
        ALTER TABLE public.sensor
            ADD CONSTRAINT fk_sensor_dron
            FOREIGN KEY (dron_id)
            REFERENCES public.dron(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 6. MISION_DRON -> MISION
-- ------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.constraint_schema = kcu.constraint_schema
        JOIN information_schema.constraint_column_usage ccu
          ON tc.constraint_name = ccu.constraint_name
         AND tc.constraint_schema = ccu.constraint_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_schema = 'public'
          AND tc.table_name = 'mision_dron'
          AND kcu.column_name = 'mision_id'
          AND ccu.table_schema = 'public'
          AND ccu.table_name = 'mision'
          AND ccu.column_name = 'id'
    ) THEN
        ALTER TABLE public.mision_dron
            ADD CONSTRAINT fk_mision_dron_mision
            FOREIGN KEY (mision_id)
            REFERENCES public.mision(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 7. MISION_DRON -> DRON
-- ------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.constraint_schema = kcu.constraint_schema
        JOIN information_schema.constraint_column_usage ccu
          ON tc.constraint_name = ccu.constraint_name
         AND tc.constraint_schema = ccu.constraint_schema
        WHERE tc.constraint_type = 'FOREIGN KEY'
          AND tc.table_schema = 'public'
          AND tc.table_name = 'mision_dron'
          AND kcu.column_name = 'dron_id'
          AND ccu.table_schema = 'public'
          AND ccu.table_name = 'dron'
          AND ccu.column_name = 'id'
    ) THEN
        ALTER TABLE public.mision_dron
            ADD CONSTRAINT fk_mision_dron_dron
            FOREIGN KEY (dron_id)
            REFERENCES public.dron(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;
    END IF;
END
$$;

-- ------------------------------------------------------------
-- 8. Indices para las columnas que participan como FK
-- ------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_dron_piloto_id
    ON public.dron(piloto_id);

CREATE INDEX IF NOT EXISTS idx_sensor_dron_id
    ON public.sensor(dron_id);

-- La PK (mision_id, dron_id) ya indexa eficientemente mision_id.
-- Se agrega un indice adicional para consultas iniciadas por dron_id.
CREATE INDEX IF NOT EXISTS idx_mision_dron_dron_id
    ON public.mision_dron(dron_id);

COMMIT;

-- ============================================================
-- 9. VERIFICACION FINAL
-- ============================================================
SELECT
    tc.table_name AS tabla_hija,
    kcu.column_name AS columna_fk,
    ccu.table_name AS tabla_padre,
    ccu.column_name AS columna_referenciada,
    tc.constraint_name AS restriccion,
    rc.delete_rule AS on_delete,
    rc.update_rule AS on_update
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
 AND tc.constraint_schema = kcu.constraint_schema
JOIN information_schema.constraint_column_usage ccu
  ON tc.constraint_name = ccu.constraint_name
 AND tc.constraint_schema = ccu.constraint_schema
JOIN information_schema.referential_constraints rc
  ON tc.constraint_name = rc.constraint_name
 AND tc.constraint_schema = rc.constraint_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND tc.table_schema = 'public'
  AND tc.table_name IN ('dron', 'sensor', 'mision_dron')
ORDER BY tc.table_name, kcu.column_name;

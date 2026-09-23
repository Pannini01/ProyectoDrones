-- ============================================================
-- MIGRACION - PERSISTENCIA DEL PATRON BRIDGE
-- PostgreSQL / pgAdmin 4
--
-- Ejecutar conectado a la base drones_db ya existente.
-- No elimina tablas ni registros.
-- Es idempotente: puede ejecutarse mas de una vez.
-- ============================================================

BEGIN;

-- 1. Validar la tabla principal.
DO $$
BEGIN
    IF to_regclass('public.dron') IS NULL THEN
        RAISE EXCEPTION
            'No existe la tabla public.dron. Verifique que esta conectado a la base drones_db.';
    END IF;
END
$$;

-- 2. Agregar la columna que recuerda el modo Bridge de cada dron.
ALTER TABLE public.dron
    ADD COLUMN IF NOT EXISTS modo_control VARCHAR(20);

-- Los drones que ya existian antes de esta version quedan inicialmente
-- en control MANUAL. Tambien se corrige cualquier valor no reconocido.
UPDATE public.dron
SET modo_control = 'MANUAL'
WHERE modo_control IS NULL
   OR modo_control NOT IN ('MANUAL', 'AUTONOMO');

-- 3. Reglas definitivas de integridad.
ALTER TABLE public.dron
    ALTER COLUMN modo_control SET DEFAULT 'MANUAL';

ALTER TABLE public.dron
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

COMMENT ON COLUMN public.dron.modo_control IS
    'Modo Bridge persistido para el dron: MANUAL o AUTONOMO.';

COMMIT;

-- ============================================================
-- VERIFICACION
-- ============================================================

-- Debe mostrar: character varying, NO y default MANUAL.
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'dron'
  AND column_name = 'modo_control';

-- Debe mostrar ck_dron_modo_control como CHECK.
SELECT
    tc.table_name AS tabla,
    tc.constraint_name AS restriccion,
    tc.constraint_type AS tipo
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'dron'
  AND tc.constraint_name = 'ck_dron_modo_control';

-- Los drones existentes deben mostrar MANUAL inicialmente.
SELECT
    id,
    serial,
    tipo,
    bateria_adicional,
    modo_control
FROM public.dron
ORDER BY serial;

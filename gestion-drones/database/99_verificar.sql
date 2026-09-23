-- EJECUTAR CONECTADO A drones_db despues de schema.sql o actualizar_llaves_foraneas.sql.
SELECT current_database() AS base_actual, current_user AS usuario_actual;

-- Tablas existentes.
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- Estructura principal del dron, Decorator y modo Bridge persistido.
SELECT id, serial, tipo, bateria_adicional, modo_control, piloto_id
FROM dron
ORDER BY serial;

-- Llaves foraneas activas y sus reglas.
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
ORDER BY tc.table_name, kcu.column_name;

-- Busqueda de registros huerfanos. Todos los resultados deben ser 0.
SELECT 'dron_piloto' AS relacion, COUNT(*) AS huerfanos
FROM dron d
LEFT JOIN piloto p ON p.id = d.piloto_id
WHERE d.piloto_id IS NOT NULL AND p.id IS NULL
UNION ALL
SELECT 'sensor_dron', COUNT(*)
FROM sensor s
LEFT JOIN dron d ON d.id = s.dron_id
WHERE d.id IS NULL
UNION ALL
SELECT 'mision_dron_mision', COUNT(*)
FROM mision_dron md
LEFT JOIN mision m ON m.id = md.mision_id
WHERE m.id IS NULL
UNION ALL
SELECT 'mision_dron_dron', COUNT(*)
FROM mision_dron md
LEFT JOIN dron d ON d.id = md.dron_id
WHERE d.id IS NULL;


-- Persistencia Bridge: todos los valores deben ser MANUAL o AUTONOMO.
SELECT modo_control, COUNT(*) AS cantidad
FROM dron
GROUP BY modo_control
ORDER BY modo_control;

-- La restriccion CHECK de Bridge debe existir.
SELECT
    tc.table_name AS tabla,
    tc.constraint_name AS restriccion,
    tc.constraint_type AS tipo
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'dron'
  AND tc.constraint_name = 'ck_dron_modo_control';

-- Composite: la estructura requerida debe contener 13 nodos.
SELECT id, nombre, tipo_nodo, padre_id, orden
FROM sensor_composite
ORDER BY COALESCE(padre_id, ''), orden, nombre;

-- Composite: no debe haber nodos con padre inexistente.
SELECT COUNT(*) AS sensores_composite_huerfanos
FROM sensor_composite hijo
LEFT JOIN sensor_composite padre ON padre.id = hijo.padre_id
WHERE hijo.padre_id IS NOT NULL
  AND padre.id IS NULL;

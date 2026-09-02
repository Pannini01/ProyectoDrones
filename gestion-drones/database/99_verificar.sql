-- EJECUTAR CONECTADO A drones_db después de schema.sql.
SELECT current_database() AS base_actual, current_user AS usuario_actual;

SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

SELECT * FROM dron ORDER BY serial;

-- EJECUTAR CONECTADO A LA BASE "postgres" COMO EL USUARIO postgres.
-- Este comando se ejecuta una sola vez.
CREATE DATABASE drones_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TEMPLATE = template0;

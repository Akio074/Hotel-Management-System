-- setup.sql
-- Run this script with a PostgreSQL superuser.
-- Example: psql -U postgres -f sql/setup.sql

-- 1. Create a specific user for the application
CREATE USER hotel_user WITH PASSWORD 'hotel_password';

-- 2. Create the standalone database
CREATE DATABASE hotel_db OWNER hotel_user;

-- Connect to the newly created database
\c hotel_db

-- 3. Grant schema usage permissions (needed especially for Postgres 15+)
GRANT ALL ON SCHEMA public TO hotel_user;

-- 4. Create Tables
CREATE TABLE guests (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT true
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    guest_id INT REFERENCES guests(id),
    room_id INT REFERENCES rooms(id),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- 5. Create Views
CREATE OR REPLACE VIEW active_reservations_view AS
SELECT 
    res.id AS reservation_id,
    res.guest_id,
    g.name AS guest_name,
    res.room_id,
    r.room_number,
    r.type AS room_type,
    res.check_in_date,
    res.check_out_date,
    res.status
FROM reservations res
JOIN guests g ON res.guest_id = g.id
JOIN rooms r ON res.room_id = r.id;

-- 6. Reassign ownership so the java application user operates without permission errors
ALTER TABLE guests OWNER TO hotel_user;
ALTER TABLE rooms OWNER TO hotel_user;
ALTER TABLE reservations OWNER TO hotel_user;
ALTER VIEW active_reservations_view OWNER TO hotel_user;

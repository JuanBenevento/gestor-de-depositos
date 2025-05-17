CREATE DATABASE IF NOT EXISTS gestorDeposito;
USE gestorDeposito;
CREATE TABLE IF NOT EXISTS producto (
                          id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(255),
                          descripcion VARCHAR(200),
                          codigo_sku BIGINT,
                          unidad_medida VARCHAR(100),
                          fecha_creacion DATETIME);
CREATE TABLE IF NOT EXISTS ubicacion (
                        id_ubicacion BIGINT AUTO_INCREMENT PRIMARY KEY,
                        codigo VARCHAR(255),
                        zona_id BIGINT,
                        capacidad_maxima INT,
                        ocupado_actual INT);

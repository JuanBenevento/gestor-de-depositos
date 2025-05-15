CREATE DATABASE IF NOT EXISTS gestorDeposito;
USE gestorDeposito;
CREATE TABLE IF NOT EXISTS producto (
                          id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(255),
                          descripcion VARCHAR(200),
                          codigo_sku BIGINT,
                          unidad_medida VARCHAR(100),
                          fecha_creacion DATETIME);

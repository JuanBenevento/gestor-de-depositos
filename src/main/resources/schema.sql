CREATE DATABASE IF NOT EXISTS gestorDeposito;
USE gestorDeposito;

CREATE TABLE IF NOT EXISTS rol (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   nombre VARCHAR (200)
);
insert into gestorDeposito.rol (nombre)
values ('ADMIN'), ('OPERATIVO');

CREATE TABLE IF NOT EXISTS usuario (
                                       id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       nombre VARCHAR(255) NOT NULL UNIQUE,
                                       contrasenia VARCHAR(255) NOT NULL,
                                       apellido VARCHAR(255) NOT NULL,
                                       email VARCHAR(255) NOT NULL,
                                       CONSTRAINT chk_email_format CHECK (email LIKE '%@%')
);

CREATE TABLE IF NOT EXISTS usuario_rol (
                                           usuario_rol_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           id_usuario BIGINT NOT NULL,
                                           id_rol BIGINT NOT NULL,
                                           FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                                           FOREIGN KEY (id_rol) REFERENCES rol(id) ON DELETE CASCADE
);
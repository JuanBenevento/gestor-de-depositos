CREATE DATABASE IF NOT EXISTS gestorDeposito;
USE gestorDeposito;

CREATE TABLE IF NOT EXISTS rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR (200)
);
insert into gestorDeposito.rol (nombre)
values ('ADMIN'), ('OPERATIVO');

package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

public class CapacidadExcedida extends RuntimeException {
    public CapacidadExcedida(String message) {
        super(message);
    }
}

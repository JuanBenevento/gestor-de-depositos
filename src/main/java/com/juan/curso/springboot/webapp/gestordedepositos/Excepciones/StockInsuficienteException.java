package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String message) {
        super(message);
    }
}
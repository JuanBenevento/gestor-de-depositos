package com.juan.curso.springboot.webapp.gestordedepositos.Excepciones;

public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

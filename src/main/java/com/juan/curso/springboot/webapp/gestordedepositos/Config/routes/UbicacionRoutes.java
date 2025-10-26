package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class UbicacionRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/ubicacion/crear",
        "GestorDeDepositos/ubicacion/actualizar",
        "/GestorDeDepositos/ubicacion/eliminar"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/ubicacion/todos",
        "/GestorDeDepositos/ubicacion/buscar"
    };
}


package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class ZonaRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/zona/crear",
        "/GestorDeDepositos/zona/actualizar",
        "/GestorDeDepositos/zona/eliminar"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/zona/buscar",
        "/GestorDeDepositos/zona/todos"
    };
}


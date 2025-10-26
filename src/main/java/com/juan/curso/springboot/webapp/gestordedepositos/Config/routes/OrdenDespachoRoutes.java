package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class OrdenDespachoRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/ordenesDeDespacho/actualizar/",
        "/GestorDeDepositos/ordenesDeDespacho/eliminarOrden/"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/ordenesDeDespacho/buscarTodos",
        "/GestorDeDepositos/ordenesDeDespacho/buscarPorId/",
        "/GestorDeDepositos/ordenesDeDespacho/crearOrden"
    };
}


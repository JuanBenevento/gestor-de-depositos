package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class OrdenRecepcionRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/ordenes/eliminarOrden"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/ordenes/crearOrdenRecepcion",
        "/GestorDeDepositos/ordenes/todos",
        "/GestorDeDepositos/ordenes/buscar",
        "/GestorDeDepositos/ordenes/actualizarEstadoOrden"
    };
}


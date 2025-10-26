package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class DetalleDespachoRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/detallesDeDespacho/actualizar/",
        "/GestorDeDepositos/detallesDeDespacho/eliminarDetalle/"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/detallesDeDespacho/buscarTodos",
        "/GestorDeDepositos/detallesDeDespacho/buscarPorId/",
        "/GestorDeDepositos/detallesDeDespacho/crearDetalle"
    };
}


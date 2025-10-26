package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class MovimientoInventarioRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/movimientoInventario/actualizar",
        "/GestorDeDepositos/movimientoInventario/eliminar"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/movimientoInventario/crearMovimiento",
        "/GestorDeDepositos/movimientoInventario/buscarTodos"
    };
}


package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class ProductoRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/producto/eliminarProducto"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/producto/todos",
        "/GestorDeDepositos/producto/buscar",
        "/GestorDeDepositos/producto/crearProducto",
        "/GestorDeDepositos/producto/actualizarProducto",
        "/GestorDeDepositos/producto/buscarPorCodigoSku"
    };
}


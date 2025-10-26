package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class InventarioRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/inventario/eliminarInventario"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/inventario/crear",
        "/GestorDeDepositos/inventario/buscarTodos",
        "/GestorDeDepositos/inventario/buscarPorCodigoSkuProducto"
    };
}


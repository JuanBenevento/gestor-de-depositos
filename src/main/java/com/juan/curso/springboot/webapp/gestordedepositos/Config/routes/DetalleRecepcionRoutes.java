package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class DetalleRecepcionRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/detalleRecepcion/todos",
        "/GestorDeDepositos/detalleRecepcion/buscarDetallePorId",
        "/GestorDeDepositos/detalleRecepcion/eliminarDetallesDeOrden"
    };
    
    public static final String[] ADMIN_OPERATIVO = {
        "/GestorDeDepositos/detalleRecepcion/actualizarDetalle",
        "/GestorDeDepositos/detalleRecepcion/eliminarDetalleConIdDet",
        "/GestorDeDepositos/detalleRecepcion/buscarDetallesPorIdOrden"
    };
}


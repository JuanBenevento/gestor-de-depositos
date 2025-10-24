package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

import java.util.Arrays;
import java.util.stream.Stream;

public class AdminOperativesRoutes {
    
    // Método para obtener todas las rutas que requieren solo ADMIN
    public static String[] getAdminRoutes() {
        return combineArrays(
            UsuarioRoutes.ADMIN_ONLY,
            OrdenDespachoRoutes.ADMIN_ONLY,
            DetalleDespachoRoutes.ADMIN_ONLY,
            ProductoRoutes.ADMIN_ONLY,
            OrdenRecepcionRoutes.ADMIN_ONLY,
            DetalleRecepcionRoutes.ADMIN_ONLY,
            ZonaRoutes.ADMIN_ONLY,
            UbicacionRoutes.ADMIN_ONLY,
            InventarioRoutes.ADMIN_ONLY,
            MovimientoInventarioRoutes.ADMIN_ONLY,
            RolRoutes.ADMIN_ONLY
        );
    }
    
    // Método para obtener todas las rutas que requieren ADMIN u OPERATIVO
    public static String[] getAdminOperativoRoutes() {
        return combineArrays(
            OrdenDespachoRoutes.ADMIN_OPERATIVO,
            DetalleDespachoRoutes.ADMIN_OPERATIVO,
            ProductoRoutes.ADMIN_OPERATIVO,
            OrdenRecepcionRoutes.ADMIN_OPERATIVO,
            DetalleRecepcionRoutes.ADMIN_OPERATIVO,
            ZonaRoutes.ADMIN_OPERATIVO,
            UbicacionRoutes.ADMIN_OPERATIVO,
            InventarioRoutes.ADMIN_OPERATIVO,
            MovimientoInventarioRoutes.ADMIN_OPERATIVO
        );
    }
    
    // Método helper para combinar arrays
    private static String[] combineArrays(String[]... arrays) {
        return Stream.of(arrays)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}


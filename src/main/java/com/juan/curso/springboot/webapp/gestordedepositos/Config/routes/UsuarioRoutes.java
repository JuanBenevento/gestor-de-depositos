package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class UsuarioRoutes {
    public static final String[] ADMIN_ONLY = {
        "/GestorDeDepositos/usuarios/crearUsuario",
        "/GestorDeDepositos/usuarios/modificarUsuario",
        "/GestorDeDepositos/usuarios/eliminarUsuario",
        "/GestorDeDepositos/usuarios/buscarUsuario",
        "/GestorDeDepositos/usuarios/buscarTodosLosUsuarios",
        "/GestorDeDepositos/usuarios/buscarPorRol"
    };
}


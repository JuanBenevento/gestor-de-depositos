package com.juan.curso.springboot.webapp.gestordedepositos.Config.routes;

public class AuthRoutes {
    public static final String[] PUBLIC = {
        "/GestorDeDepositos/login",
        "/GestorDeDepositos/cambiarContrasenia"
    };
    
    public static final String[] SWAGGER = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/swagger-ui.html"
    };
}


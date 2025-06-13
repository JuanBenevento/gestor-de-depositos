package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig() {
    }


   /* metodo para crear el primer usuario admin
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").permitAll());

        return http.build();
    }*/



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/GestorDeDepositos/login").permitAll()
                        .requestMatchers("/GestorDeDepositos/cambiarContrasenia").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/GestorDeDepositos/usuarios/crearUsuario").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/usuarios/modificarUsuario").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/usuarios/eliminarUsuario").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/usuarios/buscarUsuario").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/usuarios/buscarTodosLosUsuarios").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/usuarios/busarPorRol").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/ordenes/crearOrdenRecepcion").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenes/todos").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenes/buscar").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenes/actualizarEstadoOrden").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenes/eliminarOrden").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/todos").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/buscarDetallePorId").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/actualizarDetalle").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/eliminarDetalleConIdDet").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/eliminarDetallesDeOrden").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detalleRecepcion/buscarDetallesPorIdOrden").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenesDeDespacho/buscarTodos").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenesDeDespacho/buscarPorId/").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenesDeDespacho/actualizar/").hasAnyRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/ordenesDeDespacho/crearOrden").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ordenesDeDespacho/eliminarOrden/").hasAnyRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detallesDeDespacho/buscarTodos").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/detallesDeDespacho/buscarPorId/").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/detallesDeDespacho/actualizar/").hasAnyRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/detallesDeDespacho/crearDetalle").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/detallesDeDespacho/eliminarDetalle/").hasAnyRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/**").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/todos").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/buscar").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/crearProducto").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/eliminarProducto").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/actualizarProducto").hasAnyRole("OPERATIVO","ADMIN")
                        .requestMatchers("/GestorDeDepositos/producto/buscarPorCodigoSku").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/movimientoInventario/crearMovimiento").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/movimientoInventario/actualizar").hasAnyRole( "ADMIN")
                        .requestMatchers("/GestorDeDepositos/movimientoInventario/eliminar").hasAnyRole( "ADMIN")
                        .requestMatchers("/GestorDeDepositos/movimientoInventario/buscarTodos").hasAnyRole("OPERATIVO", "ADMIN")
                        .requestMatchers("/GestorDeDepositos/zona/crear").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/zona/actualizar").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/zona/eliminar").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/zona/buscar").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/zona/todos").hasAnyRole("ADMIN", "OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/inventario/crear").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/inventario/buscarTodos").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/inventario/buscarPorProducto").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ubicacion/todos").hasAnyRole("ADMIN","OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ubicacion/buscar").hasAnyRole("ADMIN", "OPERATIVO")
                        .requestMatchers("/GestorDeDepositos/ubicacion/crear").hasRole("ADMIN")
                        .requestMatchers("GestorDeDepositos/ubicacion/actualizar").hasRole("ADMIN")
                        .requestMatchers("/GestorDeDepositos/ubicacion/eliminar").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Ajustar según frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


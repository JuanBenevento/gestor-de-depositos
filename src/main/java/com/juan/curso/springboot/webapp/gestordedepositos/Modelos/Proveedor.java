package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_proveedor;
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El telefono no puede estar vacio")
    @Pattern(regexp = "^[0-9+\\-()\\s]{7,15}$", message = "El telefono no tiene un formato valido")
    private String telefono;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email no tiene un formato valido")
    private String email;

}

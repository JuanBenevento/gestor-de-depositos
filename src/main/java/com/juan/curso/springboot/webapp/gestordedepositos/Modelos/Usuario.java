package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String nombre;
    @NotBlank
    @Column(nullable = false)
    private String contrasenia;
    @NotBlank
    @Column(nullable = false)
    private String apellido;
    @Email
    @Column(nullable = false)
    private String email;
    @OneToOne
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"id_usuario", "id_rol"})}
    )
    private Rol rol;


}

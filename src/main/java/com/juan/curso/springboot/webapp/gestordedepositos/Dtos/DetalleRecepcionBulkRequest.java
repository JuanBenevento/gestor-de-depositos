package com.juan.curso.springboot.webapp.gestordedepositos.Dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionBulkRequest {

    @NotNull
    private Long idOrdenRecepcion;

    @NotEmpty
    @Valid
    private List<DetalleRecepcionItemDTO> detalles;
}

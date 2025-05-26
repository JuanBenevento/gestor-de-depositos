package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.UbicacionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.UbicacionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ZonaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ubicacion")
public class UbicacionController {
    private final UbicacionServiceImpl ubicacionService;
    private final ZonaServiceImpl zonaServiceImpl;

    @Autowired
    public UbicacionController(UbicacionServiceImpl ubicacionService, ZonaServiceImpl zonaServiceImpl) {
        this.ubicacionService = ubicacionService;
        this.zonaServiceImpl = zonaServiceImpl;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        List<UbicacionDTO> ubicaciones = ubicacionService.buscarTodos().orElseThrow().stream().
                map(ubicacion -> new UbicacionDTO(
                        ubicacion.getId_ubicacion(),
                        ubicacion.getCodigo(),
                        ubicacion.getZona(),
                        ubicacion.getCapacidad_maxima(),
                        ubicacion.getOcupado_actual()))
                        .collect(Collectors.toList());
        return new ResponseEntity(ubicaciones, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        Ubicacion ubicacion = ubicacionService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Ubicacion no encontrada con ID: " + id));

        UbicacionDTO ubicacionDTO = new UbicacionDTO(
                ubicacion.getId_ubicacion(),
                ubicacion.getCodigo(),
                ubicacion.getZona(),
                ubicacion.getCapacidad_maxima(),
                ubicacion.getOcupado_actual()
        );
        return new ResponseEntity(ubicacionDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody UbicacionDTO dto) {
        Zona zona = zonaServiceImpl.buscarPorId(dto.getZona().getIdZona())
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + dto.getZona().getIdZona()));

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setCodigo(dto.getCodigo());
        ubicacion.setZona(zona);
        ubicacion.setCapacidad_maxima(dto.getCapacidad_maxima());
        ubicacion.setOcupado_actual(dto.getOcupado_actual());

        ubicacionService.crear(ubicacion);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody UbicacionDTO dto) {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setCodigo(dto.getCodigo());
        ubicacion.setZona(dto.getZona());
        ubicacion.setCapacidad_maxima(dto.getCapacidad_maxima());
        ubicacion.setOcupado_actual(dto.getOcupado_actual());

        ubicacionService.actualizar(ubicacion);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        ubicacionService.eliminar(id);
        return ResponseEntity.ok("Ubicacion eliminada con éxito");
    }
}

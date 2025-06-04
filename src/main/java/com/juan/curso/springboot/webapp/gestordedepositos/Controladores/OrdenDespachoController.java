package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ClienteServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.DetalleDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenDespachoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenesDeDespacho")
public class OrdenDespachoController {
    private final OrdenDespachoServiceImpl ordenDespachoService;
    private final ClienteServiceImpl clienteServiceImpl;
    private final DetalleDespachoServiceImpl detalleDespachoService;
    private final ProductoServiceImpl productoServiceImpl;

    @Autowired
    public OrdenDespachoController(OrdenDespachoServiceImpl ordenDespachoService, ClienteServiceImpl clienteServiceImpl, DetalleDespachoServiceImpl detalleDespachoService, ProductoServiceImpl productoServiceImpl) {
        this.ordenDespachoService = ordenDespachoService;
        this.clienteServiceImpl = clienteServiceImpl;
        this.detalleDespachoService = detalleDespachoService;
        this.productoServiceImpl = productoServiceImpl;
    }

    @GetMapping("/buscarTodos")
    public ResponseEntity<?> buscarTodos() {
        List<OrdenDespacho> ordenes = ordenDespachoService.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron ordenes de despacho"));

        List<OrdenDespachoDTO> dtoList = ordenes.stream()
                .map(orden -> new OrdenDespachoDTO(
                        orden.getId_despacho(),
                        orden.getFecha_despacho(),
                        orden.getEstado(),
                        orden.getCliente(),
                        orden.getDetalle_despacho()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/buscarPorId/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));
            OrdenDespachoDTO dto = new OrdenDespachoDTO(
                    orden.getId_despacho(),
                    orden.getFecha_despacho(),
                    orden.getEstado(),
                    orden.getCliente(),
                    orden.getDetalle_despacho()
            );
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOrden")
    public ResponseEntity<?> crear(@RequestBody OrdenDespachoDTO dto) {
        OrdenDespacho orden = new OrdenDespacho();
        try {
            Optional<Cliente> cliente = clienteServiceImpl.buscarPorId(dto.getCliente().getId_cliente());

            if(cliente.isPresent()) {
               orden.setCliente(cliente.get());
            }else {
                throw new RecursoNoEncontradoException("Cliente no encontrado");
            }

            orden.setFecha_despacho(dto.getFecha_despacho());
            orden.setEstado(dto.getEstado());

            List<DetalleDespacho> detalles = dto.getDetalle_despacho().stream()
                    .map(detalleDto -> {
                        Optional<Producto> producto = productoServiceImpl.buscarPorId(detalleDto.getProducto().getIdProducto());
                        if(producto.isPresent()) {
                            DetalleDespacho detalle = new DetalleDespacho();
                            detalle.setProducto(producto.get());
                            detalle.setCantidad(detalleDto.getCantidad());
                            detalle.setOrdenDespacho(orden);
                            return detalle;
                        }else {
                            throw new RecursoNoEncontradoException("Producto no encontrado");
                        }
                    })
                    .collect(Collectors.toList());

            orden.setDetalle_despacho(detalles);

            OrdenDespacho retorno = ordenDespachoService.crearConRetorno(orden);

            OrdenDespachoDTO respuesta = new OrdenDespachoDTO();
            respuesta.setId_despacho(retorno.getId_despacho());
            respuesta.setFecha_despacho(retorno.getFecha_despacho());
            respuesta.setEstado(retorno.getEstado());
            respuesta.setCliente(retorno.getCliente());
            respuesta.setDetalle_despacho(retorno.getDetalle_despacho());

            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody OrdenDespachoDTO ordenDTO) {
        try {
            OrdenDespacho orden = ordenDespachoService.buscarPorId(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

            orden.setEstado(ordenDTO.getEstado());

            ordenDespachoService.actualizar(orden);
            return new ResponseEntity<>(orden, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminarOrden")
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            if (!ordenDespachoService.ExistePorId(id)) {
                return new ResponseEntity<>("Orden de despacho con id " + id + " no encontrada", HttpStatus.NOT_FOUND);
            }
            ordenDespachoService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada con exito", HttpStatus.OK);
        } catch (RecursoNoEncontradoException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al eliminar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.OrdenRecepcionServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProveedorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GestorDeDepositos/ordenes")
public class OrdenRecepcionController {

    private final OrdenRecepcionServiceImpl ordenRecepcionService;
    private final ProveedorServiceImpl proveedorServiceImpl;
    @Autowired
    ProductoServiceImpl productoService;

    @Autowired
    public OrdenRecepcionController(OrdenRecepcionServiceImpl ordenRecepcionService, ProveedorServiceImpl proveedorServiceImpl) {
        this.ordenRecepcionService = ordenRecepcionService;
        this.proveedorServiceImpl = proveedorServiceImpl;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> buscarTodos() {
        try {
            List<OrdenRecepcion> ordenes = ordenRecepcionService.buscarTodos()
                    .orElseThrow(() -> new RuntimeException("No se encontraron órdenes de recepción"));
            List<OrdenRecepcionDTO> ordenesDTO = ordenes.stream()
                    .map(this::toOrdenRecepcionDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(ordenesDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener órdenes: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam Long id) {
        try {
            Optional<OrdenRecepcion> orden = ordenRecepcionService.buscarPorId(id);
            if (orden.isPresent()) {
                OrdenRecepcionDTO dto = toOrdenRecepcionDTO(orden.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crearOrdenRecepcion")
    public ResponseEntity<?> crearOrdenRecepcion(@RequestBody OrdenRecepcionDTO dto) {
        try {
            OrdenRecepcion orden = new OrdenRecepcion();

            // Buscar proveedor
            Proveedor proveedor = proveedorServiceImpl.buscarPorId(dto.getIdProveedor())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            orden.setProveedor(proveedor);
            orden.setFecha(Calendar.getInstance().getTime());
            orden.setEstado(dto.getEstado());

            // Procesar detalles
            List<DetalleRecepcion> detalles = new ArrayList<>();
            for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
                DetalleRecepcion detalle = new DetalleRecepcion();
                Optional<Producto> productoExistente = Optional.ofNullable(productoService.buscarPorCodigoSKU(detalleDTO.getCodigoSku()));
                if (productoExistente.isPresent()) {
                    detalle.setProducto(productoExistente.get());
                } else {
                    Producto producto = new Producto();
                    producto.setCodigoSku(detalleDTO.getCodigoSku());
                    producto.setNombre(detalleDTO.getNombreProducto());
                    producto.setUnidad_medida(detalleDTO.getUnidadMedida());
                    producto.setDescripcion(detalleDTO.getDescripcionProducto());
                    producto.setFecha_creacion(Calendar.getInstance().getTime());
                    Producto retorno =  productoService.crearConRetorno(producto);
                    detalle.setProducto(retorno);
                }
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setOrdenRecepcion(orden);
                detalles.add(detalle);
            }

            orden.setDetalles(detalles);
            OrdenRecepcion ordenCreada = ordenRecepcionService.crearConRetorno(orden);
            OrdenRecepcionDTO ordenDTO = new OrdenRecepcionDTO();
            if(ordenCreada!= null && ordenCreada.getDetalles() != null) {
                ordenDTO = this.toOrdenRecepcionDTO(ordenCreada);
                if (ordenDTO != null) {
                    return new ResponseEntity<>(ordenDTO, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(ordenDTO, HttpStatus.CONFLICT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al crear orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> actualizar(@RequestBody OrdenRecepcionDTO ordenDTO) {
        try {
            Optional<OrdenRecepcion> existingOrden = ordenRecepcionService.buscarPorId(ordenDTO.getId_orden_recepcion());
            if (existingOrden.isEmpty()) {
                return new ResponseEntity<>("Orden no encontrada", HttpStatus.NOT_FOUND);
            }

            OrdenRecepcion orden = existingOrden.get();
            Proveedor proveedor = proveedorServiceImpl.buscarPorId(ordenDTO.getIdProveedor())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            orden.setProveedor(proveedor);
            orden.setEstado(ordenDTO.getEstado());

            // Update detalles (simplified, adjust based on requirements)
            List<DetalleRecepcion> detalles = new ArrayList<>();
            for (DetalleRecepcionDTO detalleDTO : ordenDTO.getDetalleRecepcionDTOList()) {
                Optional<Producto> productoExistente = Optional.ofNullable(productoService.buscarPorCodigoSKU(detalleDTO.getCodigoSku()));

                DetalleRecepcion detalle = new DetalleRecepcion();
                if (productoExistente.isPresent()) {
                    detalle.setProducto(productoExistente.get());
                } else {
                    Producto producto = new Producto();
                    producto.setCodigoSku(detalleDTO.getCodigoSku());
                    producto.setNombre(detalleDTO.getNombreProducto());
                    producto.setUnidad_medida(detalleDTO.getUnidadMedida());
                    Producto retorno = productoService.crearConRetorno(producto);
                    detalle.setProducto(retorno);
                }


                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setOrdenRecepcion(orden);
                detalles.add(detalle);
            }

            orden.setDetalles(detalles);
            ordenRecepcionService.actualizar(orden);
            return new ResponseEntity<>(ordenDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al actualizar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(@RequestParam Long id) {
        try {
            ordenRecepcionService.eliminar(id);
            return new ResponseEntity<>("Orden eliminada", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al eliminar orden: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to convert OrdenRecepcion to DTO
    private OrdenRecepcionDTO toOrdenRecepcionDTO(OrdenRecepcion orden) {
        OrdenRecepcionDTO dto = new OrdenRecepcionDTO();
        List<DetalleRecepcionDTO> detalles = new ArrayList<>();
        dto.setId_orden_recepcion(orden.getIdOrdenRecepcion());
        dto.setIdProveedor(orden.getProveedor().getId_proveedor());
        dto.setFecha(orden.getFecha());
        dto.setEstado(orden.getEstado());
        for(DetalleRecepcion det : orden.getDetalles()){
            DetalleRecepcionDTO detDTO = new DetalleRecepcionDTO();
            detDTO.setId_detalle_recepcion(det.getIdDetalleRecepcion());
            detDTO.setOrden(dto);
            try{
                Optional<Producto> productoEncontrado = productoService.buscarPorId(det.getProducto().getId_producto());
                if(productoEncontrado.isPresent()){
                   detDTO.setProducto(productoEncontrado.get());
                }else{
                    throw new RecursoNoEncontradoException("No se encontro el producto");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            detDTO.setCantidad(det.getCantidad());
            detalles.add(detDTO);
        }
        dto.setDetalleRecepcionDTOList(detalles);
        return dto;
    }
}
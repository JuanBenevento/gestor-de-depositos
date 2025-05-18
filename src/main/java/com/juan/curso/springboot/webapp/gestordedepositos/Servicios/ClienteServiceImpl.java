package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements GenericService<Cliente, Long>{
    private final ClienteRepositorio clienteRepositorio;

    @Autowired
    public ClienteServiceImpl(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    public Optional<List<Cliente>> buscarTodos() {
        return Optional.of(clienteRepositorio.findAll());
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepositorio.findById(id);
    }

    @Override
    public void crear(Cliente cliente) {
        clienteRepositorio.save(cliente);
    }

    @Override
    public void actualizar(Cliente cliente) {
        if (!clienteRepositorio.existsById(cliente.getId_cliente())) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + cliente.getId_cliente());
        }
        clienteRepositorio.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id);
        }
        clienteRepositorio.deleteById(id);
    }
}

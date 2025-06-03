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
        try {
            return Optional.of(clienteRepositorio.findAll());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return clienteRepositorio.findById(id);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void crear(Cliente cliente) {
        try {
            clienteRepositorio.save(cliente);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente crearConRetorno(Cliente cliente) {
        try {
            cliente = clienteRepositorio.save(cliente);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cliente;
    }

    @Override
    public Cliente actualizar(Cliente cliente) throws RecursoNoEncontradoException {
        try {
            cliente = clienteRepositorio.save(cliente);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + cliente.getId_cliente());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cliente;
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        try {
            clienteRepositorio.deleteById(id);
        }catch (RecursoNoEncontradoException e) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.geisha.service;

import com.geisha.dto.TrabajadorForm;

import java.util.List;

public interface TrabajadorService {

    List<TrabajadorForm> listarTodos();

    List<TrabajadorForm> buscar(String buscar);

    TrabajadorForm buscarPorId(Long id);

    void guardar(TrabajadorForm trabajadorForm);

    void eliminar(Long id);
}
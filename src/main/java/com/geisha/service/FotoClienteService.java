package com.geisha.service;

import org.springframework.web.multipart.MultipartFile;

public interface FotoClienteService {
    String guardar(Long personaId, MultipartFile archivo);
    void eliminar(String nombreArchivo);
    void abrirUbicacion(String nombreArchivo);
}

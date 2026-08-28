package com.geisha.service.impl;

import com.geisha.service.FotoClienteService;
import com.geisha.util.ExploradorArchivos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class FotoClienteServiceImpl implements FotoClienteService {

    // Misma carpeta configurable que expone WebConfig para poder verlas
    // en el navegador (app.fotos.directorio en application.properties)
    @Value("${app.fotos.directorio}")
    private String directorioFotos;

    private Path carpeta() {
        Path carpeta = Path.of(directorioFotos).toAbsolutePath().normalize();
        try {
            Files.createDirectories(carpeta);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de fotos: " + carpeta, e);
        }
        return carpeta;
    }

    @Override
    public String guardar(Long personaId, MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());

        // nombre unico: id de la persona + marca de tiempo, asi dos
        // clientes distintos (o dos fotos subidas para el mismo cliente
        // en momentos distintos) nunca se pisan entre si
        String nombreArchivo = personaId + "_" + System.currentTimeMillis() + extension;

        Path destino = carpeta().resolve(nombreArchivo);

        try {
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la foto del cliente", e);
        }

        return nombreArchivo;
    }

    @Override
    public void eliminar(String nombreArchivo) {

        if (!StringUtils.hasText(nombreArchivo)) {
            return;
        }

        try {
            Files.deleteIfExists(carpeta().resolve(nombreArchivo));
        } catch (IOException e) {
            // no queremos que un archivo huerfano/bloqueado tumbe la
            // operacion completa (guardar/eliminar cliente); solo se
            // deja constancia en el log
            log.warn("No se pudo borrar el archivo de foto '{}': {}", nombreArchivo, e.getMessage());
        }
    }

    @Override
    public void abrirUbicacion(String nombreArchivo) {

        if (!StringUtils.hasText(nombreArchivo)) {
            throw new IllegalStateException("Este cliente todavía no tiene una foto cargada");
        }

        Path archivo = carpeta().resolve(nombreArchivo);

        if (!Files.exists(archivo)) {
            throw new IllegalStateException("El archivo de la foto ya no existe en el disco");
        }

        /*
         * Esto abre el explorador de archivos EN EL EQUIPO DONDE CORRE
         * EL SERVIDOR, no en la computadora de quien esta viendo la
         * pagina. Tiene sentido para este proyecto porque el estudio
         * corre la app y la usa desde la misma PC (o la copia
         * portable), pero si algun dia la app se accede desde otra
         * computadora en red, este boton abriria una ventana en la PC
         * servidor, no en la del usuario.
         */
        try {
            ExploradorArchivos.abrirSeleccionando(archivo);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir el explorador de archivos en este equipo", e);
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (!StringUtils.hasText(nombreOriginal) || !nombreOriginal.contains(".")) {
            return "";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase();
    }
}
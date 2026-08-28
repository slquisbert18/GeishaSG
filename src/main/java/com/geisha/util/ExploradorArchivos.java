package com.geisha.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Abre el explorador de archivos del sistema operativo señalando un
 * archivo puntual. Usado tanto por la foto de referencia del cliente
 * como por la imagen del trabajo realizado de un pedido.
 *
 * IMPORTANTE: esto abre una ventana en el escritorio del equipo donde
 * corre el SERVIDOR, no en la computadora de quien hizo clic en el
 * botón. Tiene sentido en este proyecto porque el estudio usa la app
 * desde la misma PC que la ejecuta.
 */
public final class ExploradorArchivos {

    private ExploradorArchivos() {
    }

    public static void abrirSeleccionando(Path archivo) throws IOException {

        String sistemaOperativo = System.getProperty("os.name", "").toLowerCase();

        if (sistemaOperativo.contains("win")) {
            new ProcessBuilder("explorer.exe", "/select,\"" + archivo + "\"").start();
        } else if (sistemaOperativo.contains("mac")) {
            new ProcessBuilder("open", "-R", archivo.toString()).start();
        } else {
            // Linux: no hay una forma estandar de "seleccionar" un
            // archivo, se abre la carpeta que lo contiene
            new ProcessBuilder("xdg-open", archivo.getParent().toString()).start();
        }
    }
}
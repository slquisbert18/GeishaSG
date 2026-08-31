package com.geisha.util;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Abre, en el escritorio del equipo donde corre el servidor, la carpeta
 * que contiene un archivo puntual. Usado tanto por la foto de referencia
 * del cliente como por la imagen del trabajo realizado de un pedido.
 *
 * IMPORTANTE: esto abre una ventana en el escritorio del equipo donde
 * corre el SERVIDOR, no en la computadora de quien hizo clic en el
 * botón. Tiene sentido en este proyecto porque el estudio usa la app
 * desde la misma PC que la ejecuta.
 *
 * NOTA sobre por que se abre la carpeta y no se "selecciona" el archivo:
 * la primera version invocaba "explorer.exe /select,..." como proceso
 * nuevo. En Windows, explorer.exe corre como un proceso unico (shell):
 * al lanzarlo de nuevo con argumentos, Windows normalmente NO abre una
 * ventana nueva, sino que reenvia la peticion a una ventana ya abierta
 * de Explorador - y si esa ventana ya estaba en otra carpeta (ej.
 * "Documentos"), Windows la trae al frente ignorando el "/select". Es un
 * comportamiento conocido y documentado del shell (no es un problema de
 * comillas ni de escapado), y no es arreglable de forma confiable solo
 * ajustando el comando. Por eso se cambio a abrir la carpeta contenedora
 * con la API Desktop de Java: es multiplataforma y no depende de invocar
 * explorer.exe a mano, aunque a cambio ya no se resalta automaticamente
 * el archivo dentro de la carpeta.
 */
public final class ExploradorArchivos {

    private ExploradorArchivos() {
    }

    public static void abrirSeleccionando(Path archivo) throws IOException {

        Path carpeta = archivo.toAbsolutePath().getParent();

        if (carpeta == null) {
            throw new IOException("No se pudo determinar la carpeta del archivo: " + archivo);
        }

        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            throw new IOException("Este equipo no tiene un escritorio grafico disponible para abrir carpetas");
        }

        Desktop.getDesktop().open(carpeta.toFile());
    }
}
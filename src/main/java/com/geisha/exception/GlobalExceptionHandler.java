package com.geisha.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/*
 * Esta clase captura errores que ocurren
 * en cualquier Controller de la aplicación.
 *
 * En lugar de mostrar errores técnicos,
 * devuelve mensajes amigables al usuario.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // captura errores de nombre duplicado.
    @ExceptionHandler(NombreDuplicadoException.class)
    public String manejarNombreDuplicado(NombreDuplicadoException ex, Model model){
        // Enviamos el mensaje personalizado hacia la vista
        model.addAttribute("mensajeError", ex.getMessage());

        // Mostramos una página de error
        return "error";
    }



    /*
     * Captura cualquier error no controlado.
     *
     * Ejemplo:
     *
     * - Error de conexión BD.
     * - Error inesperado.
     */
    @ExceptionHandler(Exception.class)
    public String manejarErrorGeneral(Exception ex, Model model){
        model.addAttribute("mensajeError", "Ocurrió un error inesperado. Por favor intente nuevamente.");
        return "error";
    }

}
package com.geisha.exception;

/*
 * Excepción personalizada para errores
 * relacionados con datos duplicados.
 *
 * Ejemplo:
 *
 * Intentar registrar dos instituciones
 * con el mismo nombre.
 *
 * Al crear nuestra propia excepción,
 * podemos diferenciar este error de
 * otros errores del sistema.
 */
public class NombreDuplicadoException extends RuntimeException {

    private String campo;
    // Constructor que recibe el mensaje que será mostrado posteriormente al usuario
    public NombreDuplicadoException(String campo, String mensaje){
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo(){
        return campo;
    }

}
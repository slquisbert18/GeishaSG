package com.geisha.dto;

import com.geisha.entity.Persona;
import lombok.Getter;

/*
 * Representa a un cliente cuyo cumpleanos cae dentro de la ventana de dias
 * que se este consultando (ver NotificacionService). "diasRestantes" en 0
 * significa que es HOY.
 */
@Getter
public class CumpleanosProximo {

    private final Persona persona;
    private final int diasRestantes;

    public CumpleanosProximo(Persona persona, int diasRestantes) {
        this.persona = persona;
        this.diasRestantes = diasRestantes;
    }
}
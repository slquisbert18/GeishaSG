package com.geisha.service;

import com.geisha.dto.CumpleanosProximo;
import com.geisha.entity.Persona;
import com.geisha.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final PersonaRepository personaRepository;

    /*
     * Devuelve los clientes (personas sin cuenta de Usuario) cuyo
     * cumpleanos cae HOY o dentro de los proximos "diasSiguientes" dias.
     *
     * Se compara solo mes y dia (java.time.MonthDay), nunca el anio: asi
     * "cumple 30 anios" funciona igual que "cumple 5 anios". El calculo
     * se hace en Java en vez de en una consulta SQL con funciones de
     * fecha, para que sea facil de leer y no dependa del motor de BD.
     */
    public List<CumpleanosProximo> obtenerProximos(int diasSiguientes) {

        LocalDate hoy = LocalDate.now();

        return personaRepository.listarClientes().stream()
                .filter(persona -> persona.getFechaNacimiento() != null)
                .map(persona -> {
                    int dias = diasHastaProximoCumpleanos(persona.getFechaNacimiento(), hoy);
                    return new CumpleanosProximo(persona, dias);
                })
                .filter(cp -> cp.getDiasRestantes() >= 0 && cp.getDiasRestantes() <= diasSiguientes)
                .sorted(Comparator.comparingInt(CumpleanosProximo::getDiasRestantes))
                .toList();
    }

    /*
     * Cuantos dias faltan desde "hoy" hasta la proxima vez que se repita
     * el mes/dia de nacimiento. 0 = cumple hoy. Si ese mes/dia ya paso
     * este anio, se calcula para el anio siguiente (por eso el ajuste
     * +1 anio cuando corresponde).
     */
    private int diasHastaProximoCumpleanos(LocalDate fechaNacimiento, LocalDate hoy) {

        MonthDay cumple = MonthDay.from(fechaNacimiento);

        LocalDate proximoCumpleanos = cumple.atYear(hoy.getYear());

        // 29 de febrero en un anio no bisiesto: MonthDay.atYear ajusta al
        // 1 de marzo automaticamente, evitando una excepcion aqui
        if (proximoCumpleanos.isBefore(hoy)) {
            proximoCumpleanos = cumple.atYear(hoy.getYear() + 1);
        }

        return (int) ChronoUnit.DAYS.between(hoy, proximoCumpleanos);
    }
}
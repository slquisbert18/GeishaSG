package com.geisha.dto;

import com.geisha.entity.Persona;
import com.geisha.entity.Usuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrabajadorForm {

    private Persona persona;
    private Usuario usuario;
}
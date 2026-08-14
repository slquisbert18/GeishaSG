/*
=========================================================
                GEISHA - APP.JS
=========================================================

Este archivo contiene el comportamiento general de la
interfaz del sistema.

Actualmente implementa:

✓ Sidebar colapsable
✓ Modal reutilizable
✓ Confirmaciones
✓ Mensajes de éxito
✓ Mensajes de error

Más adelante también contendrá:

- Notificaciones
- Toasts
- Paginación dinámica
- Filtros
- Etc.

=========================================================
*/


/*=======================================================
                SIDEBAR
=======================================================*/
const sidebar = document.querySelector(".sidebar");
const btnMenu = document.getElementById("btnMenu");
// Contrae o expande el menú lateral
if(btnMenu){

    btnMenu.addEventListener("click",function(){

        sidebar.classList.toggle("collapsed");

    });

}


// REFERENCIAS DEL MODAL
const modal = document.getElementById("modal");
const modalIcon = document.getElementById("modalIcon");
const modalTitle = document.getElementById("modalTitle");
const modalMessage = document.getElementById("modalMessage");
const btnAceptar = document.getElementById("btnAceptar");
const btnCancelar = document.getElementById("btnCancelar");

// REFERENCIAS DEL MODAL DE ESPECIFICACION FOTOGRAFICA
const modalFoto = document.getElementById("modalFoto");
const fotoTitulo = document.getElementById("fotoTitulo");
const fotoSubtitulo = document.getElementById("fotoSubtitulo");
const fotoContenido = document.getElementById("fotoContenido");
const fotoVacio = document.getElementById("fotoVacio");
const btnCerrarFoto = document.getElementById("btnCerrarFoto");
const btnCerrarFotoSup = document.getElementById("btnCerrarFotoSup");
const btnEditarFoto = document.getElementById("btnEditarFoto");
const btnAgregarFoto = document.getElementById("btnAgregarFoto");

let tramiteSeleccionado = null;

// ABRIR MODAL
function mostrarModal(config){
    // Título
    modalTitle.textContent = config.titulo;

    // Mensaje
    modalMessage.textContent = config.mensaje;

    // Texto botones
    btnAceptar.textContent =
        config.textoAceptar || "Aceptar";

    btnCancelar.textContent =
        config.textoCancelar || "Cancelar";

    // Mostrar u ocultar botón cancelar
    btnCancelar.style.display =
        config.mostrarCancelar
            ? "inline-flex"
            : "none";

    // Color botón principal
    btnAceptar.style.background = config.colorBoton || "#2F241D";

    // Cambiar icono según tipo
    switch(config.tipo){
        case "success":
            modalIcon.innerHTML = '<i class="bi bi-check-circle-fill"></i>';
            modalIcon.style.color="#2E7D32";
            break;

        case "error":
            modalIcon.innerHTML = '<i class="bi bi-x-circle-fill"></i>';
            modalIcon.style.color="#C62828";
            break;

        case "warning":
            modalIcon.innerHTML = '<i class="bi bi-exclamation-triangle-fill"></i>';
            modalIcon.style.color="#F9A825";
            break;

        case "info":
            modalIcon.innerHTML = '<i class="bi bi-info-circle-fill"></i>';
            modalIcon.style.color="#1976D2";
            break;
    }

    // Acción botón aceptar
    btnAceptar.onclick=function(){
        cerrarModal();
        if(config.onAceptar){
            config.onAceptar();
        }
    };

    // Acción botón cancelar
    btnCancelar.onclick=function(){
        cerrarModal();
    };

    // Mostrar modal
    modal.classList.add("show");

}

// abrir modal especificaciones
function abrirModalFoto(){
    modalFoto.classList.add("show");
}

// CERRAR MODAL
function cerrarModal(){
    modal.classList.remove("show");
}

// cerrar modal especificaciones
function cerrarModalFoto(){
    modalFoto.classList.remove("show");
}

btnCerrarFoto.onclick=cerrarModalFoto;
btnCerrarFotoSup.onclick=cerrarModalFoto;

btnAgregarFoto.addEventListener("click", function(){
    window.location.href = "/especificaciones/nuevo/" + tramiteSeleccionado;
});

btnEditarFoto.addEventListener("click", function(){
    window.location.href = "/especificaciones/editar/" + tramiteSeleccionado;
})

// mostrar (si existe) especificacion fotografica de un tramite
function mostrarEspecificacion(datos){
    fotoTitulo.textContent = "Especificación fotográfica";
    fotoSubtitulo.textContent = "Trámite: " + datos.tramite;
    fotoContenido.style.display = "block";
    fotoVacio.style.display = "none";
    btnEditarFoto.style.display = "inline-flex";
    btnAgregarFoto.style.display = "none";

    document.getElementById("detalleAncho").textContent = datos.ancho;
    document.getElementById("detalleAlto").textContent = datos.alto;
    document.getElementById("detalleCantidad").textContent = datos.cantidad;
    document.getElementById("detalleColor").textContent = datos.color;
    document.getElementById("detallePapel").textContent = datos.papel;
    document.getElementById("detalleRgb").textContent = datos.rgb;
    document.getElementById("detalleHex").textContent = datos.hex;
    document.getElementById("detalleResolucion").textContent = datos.resolucion;
    document.getElementById("detalleMargen").textContent = datos.margen;
    document.getElementById("detalleObservaciones").textContent = datos.observaciones;

    abrirModalFoto();
}

// en caso de no tener especificacion fotografica de un tramite
function mostrarSinEspecificacion(nombreTramite){
    fotoTitulo.textContent = "Especificación fotográfica";
    fotoSubtitulo.textContent = "Trámite: " + nombreTramite;
    fotoContenido.style.display = "none";
    fotoVacio.style.display = "block";
    btnEditarFoto.style.display = "none";
    btnAgregarFoto.style.display = "inline-flex";
    abrirModalFoto();
}

//ATAJOS

// Mensaje de éxito
function mostrarExito(mensaje){
    mostrarModal({
        tipo:"success",
        titulo:"Operación realizada",
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#2E7D32"
    });
}

// Mensaje de error
function mostrarError(mensaje){
    mostrarModal({
        tipo:"error",
        titulo:"Ha ocurrido un error",
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#C62828"
    });
}

// Mensaje informativo
function mostrarInformacion(titulo,mensaje){
    mostrarModal({
        tipo:"info",
        titulo:titulo,
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#1976D2"
    });
}

// Confirmación
function confirmar(titulo, mensaje, onAceptar){
    mostrarModal({
        tipo:"warning",
        titulo:titulo,
        mensaje:mensaje,
        textoAceptar:"Eliminar",
        textoCancelar:"Cancelar",
        mostrarCancelar:true,
        colorBoton:"#C62828",
        onAceptar:onAceptar
    });
}

/*
=======================================================
MENSAJES ENVIADOS POR SPRING
=======================================================
*/
document.addEventListener("DOMContentLoaded", function () {
    const mensaje = document.getElementById("mensajeExito");
    if (mensaje) {
        mostrarExito(
            mensaje.dataset.mensaje
        );
    }
});

/*
=======================================================
CONFIRMACIÓN DE ELIMINACIÓN
=======================================================
*/

document.addEventListener("DOMContentLoaded", function () {
    // Buscamos todos los botones eliminar
    const botonesEliminar =
        document.querySelectorAll(".btn-eliminar");

    botonesEliminar.forEach(function(boton){
        boton.addEventListener("click", function(e){

            e.preventDefault();

            const id = this.dataset.id;
            const nombre = this.dataset.nombre;
            const url = this.dataset.url;
            const entidad = this.dataset.entidad;
            confirmar(
                "Eliminar " + entidad,
                "¿Está seguro de eliminar el " + entidad +
                " \"" + nombre + "\"?\n\nEsta acción no podrá deshacerse.",

                function(){
                    window.location.href = url + "/" + id;
                }
            );
        });
    });
});

// ESPECIFICACION FOTOGRAFICA
document.addEventListener("DOMContentLoaded", function(){
    const botonesFoto = document.querySelectorAll(".btn-foto");
    botonesFoto.forEach(function (boton){
        boton.addEventListener("click", function(){
            const tramiteId = this.dataset.id;
            cargarEspecificacion(tramiteId);
        });
    });
});

async function cargarEspecificacion(tramiteId){
    tramiteSeleccionado = tramiteId;
    const boton = document.querySelector('.btn-foto[data-id="' + tramiteId + '"]');
    const nombreTramite = boton.dataset.tramite;
    try{
        const respuesta = await fetch("/tramites/" + tramiteId + "/especificacion");

        // el tramite no tiene especificacion
        if(respuesta.status === 404){
            // const tramite = document.querySelector('[data-id="' + tramiteId + '"]');
            mostrarSinEspecificacion(nombreTramite);
            return;
        }

        // otro error del servidor
        if(!respuesta.ok){
            throw new Error("Error al consultar la especificacion");
        }

        const especificacion = await respuesta.json();

        // abrimos el modal
        if(especificacion){
            mostrarEspecificacion({
                //tramite: document.querySelector('[data-id="'+tramiteId+'"]').dataset.tramite,
                tramite: nombreTramite,
                ancho: especificacion.ancho + " cm",
                alto: especificacion.alto + " cm",
                cantidad: especificacion.cantidad,
                color: especificacion.colorFondo,
                papel: especificacion.tipoPapel,
                rgb: especificacion.codigoRgb ?? "-",
                hex: especificacion.codigoHex ?? "-",
                resolucion: especificacion.resolucion ?
                    especificacion.resolucion + " dpi" :
                    "-",
                margen: especificacion.margen ?
                    especificacion.margen + " mm" :
                    "-",
                observaciones: especificacion.observaciones ?? "-"
            });
        }
        else{
            //mostrarSinEspecificacion(document.querySelector('[data-id="'+tramiteId+'"]').dataset.tramite);
            mostrarSinEspecificacion(nombreTramite);
        }

    }
    catch(error){
        console.error(error);
        mostrarError("No fue posible obtener la especificacion fotografica");
    }
}
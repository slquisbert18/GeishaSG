package com.geisha.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * Genera los PDFs de "imprimir/exportar" de cada CRUD. Se construyen con
 * OpenPDF directo en Java (sin pasar por HTML/CSS), asi el resultado no
 * depende del navegador ni de como se vea la pagina en pantalla.
 *
 * Dos formatos cubren todos los casos que se piden en el proyecto:
 *  - generarListado: una tabla (para el boton "Imprimir" del listado
 *    completo de un CRUD).
 *  - generarDetalle: una ficha con secciones de pares etiqueta/valor
 *    (para el PDF de un registro individual, ej. el detalle de un pedido).
 */
@Service
public class PdfService {

    // colores/fuentes del documento, consistentes con el color primario de la app (--color-primary)
    private static final Color COLOR_PRIMARIO = new Color(198, 40, 40);
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);

    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_PRIMARIO);
    private static final Font FUENTE_SUBTITULO = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);
    private static final Font FUENTE_SECCION = new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_PRIMARIO);
    private static final Font FUENTE_ENCABEZADO_TABLA = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
    private static final Font FUENTE_ETIQUETA = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font FUENTE_CELDA = new Font(Font.HELVETICA, 9, Font.NORMAL);

    /*
     * PDF de listado: titulo + una tabla con encabezados y filas.
     * Cada elemento de "filas" debe tener el mismo tamano que
     * "encabezados" (una fila = un valor de texto por columna, en orden).
     */
    public byte[] generarListado(String titulo, List<String> encabezados, List<List<String>> filas) {

        // horizontal (landscape): los listados suelen tener varias
        // columnas y no entran comodas en una hoja vertical
        Document documento = new Document(PageSize.A4.rotate(), 30, 30, 50, 30);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarEncabezado(documento, titulo);

            PdfPTable tabla = new PdfPTable(encabezados.size());
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(15);

            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado, FUENTE_ENCABEZADO_TABLA));
                celda.setBackgroundColor(COLOR_PRIMARIO);
                celda.setPadding(6);
                tabla.addCell(celda);
            }

            for (List<String> fila : filas) {
                for (String valor : fila) {
                    PdfPCell celda = new PdfPCell(new Phrase(valor != null ? valor : "-", FUENTE_CELDA));
                    celda.setPadding(5);
                    tabla.addCell(celda);
                }
            }

            documento.add(tabla);
            documento.close();

        } catch (DocumentException e) {
            // no deberia pasar con datos de texto simple; si pasa, es un
            // problema real (documento corrupto, fuente invalida, etc)
            throw new RuntimeException("No se pudo generar el PDF", e);
        }

        return salida.toByteArray();
    }

    /*
     * PDF de detalle: titulo + una o varias secciones, cada una con su
     * propio subtitulo y una tabla de dos columnas (etiqueta / valor).
     */
    public byte[] generarDetalle(String titulo, List<SeccionDetalle> secciones) {

        Document documento = new Document(PageSize.A4, 40, 40, 50, 40);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarEncabezado(documento, titulo);

            for (SeccionDetalle seccion : secciones) {

                Paragraph subtitulo = new Paragraph(seccion.titulo(), FUENTE_SECCION);
                subtitulo.setSpacingBefore(15);
                subtitulo.setSpacingAfter(6);
                documento.add(subtitulo);

                PdfPTable tabla = new PdfPTable(2);
                tabla.setWidthPercentage(100);
                tabla.setWidths(new float[]{1, 2});

                for (String[] par : seccion.pares()) {

                    PdfPCell etiqueta = new PdfPCell(new Phrase(par[0], FUENTE_ETIQUETA));
                    etiqueta.setPadding(5);
                    etiqueta.setBackgroundColor(COLOR_GRIS_CLARO);
                    tabla.addCell(etiqueta);

                    PdfPCell valor = new PdfPCell(new Phrase(par[1] != null ? par[1] : "-", FUENTE_CELDA));
                    valor.setPadding(5);
                    tabla.addCell(valor);
                }

                documento.add(tabla);
            }

            documento.close();

        } catch (DocumentException e) {
            throw new RuntimeException("No se pudo generar el PDF", e);
        }

        return salida.toByteArray();
    }

    private void agregarEncabezado(Document documento, String titulo) throws DocumentException {
        documento.add(new Paragraph(titulo, FUENTE_TITULO));
        documento.add(new Paragraph(
                "Generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                FUENTE_SUBTITULO
        ));
    }

    /*
     * Arma la respuesta HTTP para cualquiera de los dos PDFs de arriba.
     * "inline" (no "attachment"): el navegador lo abre en una pestana
     * nueva para verlo/imprimirlo directo, en vez de forzar la descarga.
     */
    public ResponseEntity<byte[]> responder(byte[] pdf, String nombreArchivo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // una seccion de la ficha de detalle: un subtitulo + sus pares etiqueta/valor
    public record SeccionDetalle(String titulo, List<String[]> pares) {
    }
}
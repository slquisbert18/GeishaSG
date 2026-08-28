package com.geisha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeishaApplication {

    public static void main(String[] args) {

        /*
         * Spring Boot fija java.awt.headless=true muy temprano en el
         * arranque (antes de leer application.properties), por eso
         * "spring.main.headless=false" en el archivo de propiedades no
         * alcanza a aplicarse a tiempo. Se fija aca, ANTES de
         * SpringApplication.run(), para que el boton "Examinar" del
         * formulario de pedido pueda abrir el explorador de archivos
         * nativo de Windows.
         */
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(GeishaApplication.class, args);
    }

}
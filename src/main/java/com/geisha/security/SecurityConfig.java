package com.geisha.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion central de seguridad del sistema.
 *  1) Define que rutas son publicas (login, css/js/imagenes) y cuales
 *     requieren estar autenticado
 *  2) Restringe la gestion de usuarios (/trabajadores/**) solo al rol
 *     ADMINISTRADOR
 *  3) Configura el formulario de login y el logout.
 *  4) Publica el PasswordEncoder (BCrypt) que se usa tanto para
 *     registrar contraseñas como para verificarlas en el login.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    // BCrypt genera un hash distinto cada vez aunque la contraseña sea la
    // misma (usa "salt" aleatorio), y es imposible revertirlo a texto
    // plano. Para verificar, se vuelve a hashear el intento de login y
    // se compara contra el hash guardado.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Le dice a Spring Security COMO validar un login:
    // - de donde saca los datos del usuario (usuarioDetailsService)
    // - como comparar la contraseña (passwordEncoder)
    // Sin esto Spring Boot igual lo arma automaticamente si detecta un
    // unico UserDetailsService y un unico PasswordEncoder, pero dejarlo
    // explicito hace mas claro de donde sale la logica de autenticacion.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // reglas de autorizacion, se evaluan en orden: la primera que
                // coincida con la URL solicitada es la que aplica
                .authorizeHttpRequests(auth -> auth
                        // recursos estaticos y pantalla de login: sin sesion
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()
                        // gestion de usuarios (crear/editar/eliminar trabajadores
                        // y administradores): solo administrador
                        .requestMatchers("/trabajadores/**").hasRole("ADMINISTRADOR")
                        // el resto de la app: cualquier usuario logueado
                        // (ADMINISTRADOR o TRABAJADOR)
                        .anyRequest().authenticated()
                )
                // pantalla de login propia en vez de la generica de Spring
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // URL a la que se envia el POST del formulario
                        .defaultSuccessUrl("/", true) // a donde ir tras loguearse bien
                        .failureUrl("/login?error") // a donde ir si las credenciales son invalidas
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
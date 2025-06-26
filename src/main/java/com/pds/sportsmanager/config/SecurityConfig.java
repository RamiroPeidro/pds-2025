package com.pds.sportsmanager.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para encriptar y verificar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager que Spring Security monta internamente,
     * para que puedas inyectarlo y usarlo en tu AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Define la cadena de filtros de seguridad: qué rutas son públicas,
     * cuáles requieren estar autenticadas, CSRF deshabilitado y HTTP Basic.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF (útil si consumes tu API desde un cliente REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Desactiva ciertas cabeceras para permitir, por ejemplo, h2-console en un iframe
                .headers(AbstractHttpConfigurer::disable)

                // Configura qué rutas son públicas y cuáles requieren autenticación
                .authorizeHttpRequests(authz -> authz
                        // Endpoints de auth, docs y health check
                        .requestMatchers("/api/auth/registrar").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Cualquier otro /api/** debe estar autenticado
                        .requestMatchers("/api/**").authenticated()

                        // Resto de rutas (estáticas, otras views) también autenticadas
                        .anyRequest().authenticated()
                )

                // Usa HTTP Basic con el Customizer (no con el método deprecated)
                .httpBasic(withDefaults());  //  [oai_citation:0‡docs.spring.io](https://docs.spring.io/spring-security/reference/api/java/deprecated-list.html?utm_source=chatgpt.com)

        return http.build();
    }
}
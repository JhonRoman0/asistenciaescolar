package asistenciaescolar.asistenciaescolar.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig{


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (Crucial para APIs REST independientes)
                .csrf(csrf -> csrf.disable())
                // 2. Configurar CORS para permitir que Next.js se conecte
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Archivos HTML y estáticos
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                        .requestMatchers("/api/usuarios/perfil").authenticated()

                        // 2. Endpoints de Usuarios
                        .requestMatchers(HttpMethod.GET, "/api/roles","/api/roles/**").permitAll()
                        .requestMatchers("/api/usuarios/roles-disponibles").permitAll()
                        .requestMatchers("/api/usuarios/listar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()

                        // 3. Endpoints de Roles
                        .requestMatchers(HttpMethod.POST, "/api/roles").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/roles/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/**").permitAll()

                        // 4. NUEVO: Endpoints de Alumnos (Permisos explícitos para el CRUD)
                        .requestMatchers(HttpMethod.GET, "/api/alumnos", "/api/alumnos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/alumnos", "/api/alumnos/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/alumnos", "/api/alumnos/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/alumnos", "/api/alumnos/**").permitAll()

                        // 5. Enpoints de Modulos
                        .requestMatchers(HttpMethod.GET,"/api/modulos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/modulos/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/modulos/**").permitAll()

                        // 6. Enpointd de apoderado
                        .requestMatchers("/api/apoderados", "/api/apoderados/**").permitAll()

                        // 7: Enpointd de Turno
                        .requestMatchers("/api/turnos/**").permitAll()

                        // 8: Enpointd de Colegio
                        .requestMatchers("/api/colegios/**").permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración explícita de CORS para tu Front-end en Next.js
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Origen de Next.js
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
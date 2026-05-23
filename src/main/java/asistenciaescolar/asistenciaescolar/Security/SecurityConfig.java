package asistenciaescolar.asistenciaescolar.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Archivos HTML y estáticos
                        .requestMatchers(
                                "/login.html",
                                "/dashboard.html",
                                "/registro_usuario.html",
                                "/lista_usuarios.html",
                                "/registro_alumno.html", // Agregado
                                "/lista_alumno.html",    // Agregado
                                "/static/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // 2. Endpoints de Usuarios y Roles
                        .requestMatchers(HttpMethod.GET, "/api/roles","/api/roles/**").permitAll()
                        .requestMatchers("/api/usuarios/roles-disponibles").permitAll()
                        .requestMatchers("/api/usuarios/listar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").permitAll()

                        // 3. NUEVO: Endpoints de Alumnos (Permisos para el CRUD)
                        // Permitimos GET para cargar selects (grados/secciones) y la tabla
                        .requestMatchers(HttpMethod.GET, "/api/alumnos/**").permitAll()
                        // Permitimos POST para registrar alumnos
                        .requestMatchers(HttpMethod.POST, "/api/alumnos/**").permitAll()
                        // Permitimos PUT para actualizar
                        .requestMatchers(HttpMethod.PUT, "/api/alumnos/**").permitAll()

                        // 4. Restricciones específicas
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("admin")
                        .requestMatchers("/api/usuarios/perfil").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard.html", true)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
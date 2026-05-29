package br.com.wedding_site_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Públicas
                        .requestMatchers(HttpMethod.GET,  "/api/admin/presentes-recebidos").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/convites/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/convites/*/confirmar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/pix").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/pagamentos/pix/*/status").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/cartao").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/webhook").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/checkout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/api/convites").permitAll()
                        .requestMatchers(HttpMethod.PATCH,  "/api/convites/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/convites/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/api/catalogo").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/catalogo/**").permitAll()
                        .requestMatchers(HttpMethod.POST,   "/api/convites/convidados").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pagamentos/test-email/*").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()




                        // Admin
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
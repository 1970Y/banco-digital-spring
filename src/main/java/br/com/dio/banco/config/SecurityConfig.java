package br.com.dio.banco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/contas", "/contas/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/contas/*/depositos", "/contas/*/saques", "/transferencias").authenticated()
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // API de demonstração para curl/Postman; não habilitar para uso no navegador sem proteção CSRF.
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean UserDetailsService usuarios(PasswordEncoder encoder,
        @Value("${demo.venilton-password}") String senhaVenilton,
        @Value("${demo.maria-password}") String senhaMaria) {
        return new InMemoryUserDetailsManager(
            User.withUsername("venilton").password(encoder.encode(senhaVenilton)).roles("CLIENTE").build(),
            User.withUsername("maria").password(encoder.encode(senhaMaria)).roles("CLIENTE").build());
    }
}

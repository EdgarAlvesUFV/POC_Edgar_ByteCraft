package br.com.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Configuration: Indica que esta classe contém definições de beans do Spring.
 * @EnableWebSecurity: Habilita a segurança web no aplicativo Spring.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Um @Bean é um objeto que é instanciado, montado e gerenciado pelo Contêiner de Inversão de Controle (IoC) do Spring.
     * O que acontece é que o Spring cria o objeto (injetando dependências necessárias) e gerencia
     * o ciclo de vida de dele (quando é criado e destruído).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura a cadeia de filtros de segurança para o aplicativo.
     * Permite todas as requisições para caminhos que comecem com "/api/" sem autenticação.
     *
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

package com.example.LoginPUC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Define quais rotas são públicas e quais exigem autenticação.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/recoverpassword", "/resetpassword").permitAll()
                        .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Formulário de login processado pelo próprio Spring Security.
                .formLogin(form -> form
                        .loginPage("/login")            // GET exibido pelo nosso controller
                        .loginProcessingUrl("/login")   // POST tratado pelo framework
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // Encerramento de sessão.
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        // Necessário para o console do H2 funcionar (usa frames e não envia token CSRF).
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}

package com.travelapp.config;

import com.travelapp.service.ClientCompteService;
import com.travelapp.service.UtilisateurService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation
    .Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication
    .AuthenticationManager;
import org.springframework.security.authentication
    .ProviderManager;
import org.springframework.security.authentication.dao
    .DaoAuthenticationProvider;
import org.springframework.security.config.annotation
    .web.builders.HttpSecurity;
import org.springframework.security.config.annotation
    .web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt
    .BCryptPasswordEncoder;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.security.web
    .SecurityFilterChain;
import org.springframework.security.web.util.matcher
    .AntPathRequestMatcher;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UtilisateurService
        utilisateurService;
    private final ClientCompteService
        clientCompteService;

    public SecurityConfig(
            @Lazy UtilisateurService
                utilisateurService,
            @Lazy ClientCompteService
                clientCompteService) {
        this.utilisateurService =
            utilisateurService;
        this.clientCompteService =
            clientCompteService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ===== PROVIDER ADMIN =====
    @Bean
    public DaoAuthenticationProvider adminProvider() {
        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();
        provider.setUserDetailsService(
            utilisateurService);
        provider.setPasswordEncoder(
            passwordEncoder());
        return provider;
    }

    // ===== PROVIDER CLIENT =====
    @Bean
    public DaoAuthenticationProvider clientProvider() {
        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider();
        provider.setUserDetailsService(
            clientCompteService);
        provider.setPasswordEncoder(
            passwordEncoder());
        return provider;
    }

    // ===== SECURITY ADMIN =====
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(
            HttpSecurity http) throws Exception {

        http
            .securityMatcher(
                "/login",
                "/logout",
                "/dashboard/**",
                "/clients/**",
                "/voyages/**",
                "/reservations/**",
                "/paiements/**",
                "/destinations/**",
                "/admin/**"
            )
            .authenticationProvider(
                adminProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/error"
                ).permitAll()
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl(
                    "/dashboard", true)
                .failureUrl(
                    "/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(
                    new AntPathRequestMatcher(
                        "/logout"))
                .logoutSuccessUrl(
                    "/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }

    // ===== SECURITY CLIENT =====
    @Bean
    @Order(2)
    public SecurityFilterChain clientFilterChain(
            HttpSecurity http) throws Exception {

        http
            .securityMatcher("/client/**")
            .authenticationProvider(
                clientProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/client",
                    "/client/",
                    "/client/accueil",
                    "/client/voyages",
                    "/client/voyages/**",
                    "/client/inscription",
                    "/client/connexion"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/client/connexion")
                .loginProcessingUrl(
                    "/client/connexion")
                .defaultSuccessUrl(
                    "/client/accueil", true)
                .failureUrl(
                    "/client/connexion"
                    + "?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(
                    new AntPathRequestMatcher(
                        "/client/logout"))
                .logoutSuccessUrl(
                    "/client/connexion"
                    + "?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
package org.ascaso.webfluxtaskmanager.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * HTTP Basic con dos roles: USER lee, crea y modifica; solo ADMIN borra.
 * Los usuarios salen de configuración ({@code app.security.users}), nunca del código.
 */
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(SecurityConfig.AppSecurityProperties.class)
public class SecurityConfig {

    @ConfigurationProperties("app.security")
    public record AppSecurityProperties(List<AppUser> users) {
        public record AppUser(String name, String password, List<String> roles) {
        }
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // API sin sesión ni formularios
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/index.html", "/css/**", "/js/**", "/favicon.ico").permitAll()
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .pathMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated())
                .httpBasic(basic -> { })
                .build();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService(AppSecurityProperties properties) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new MapReactiveUserDetailsService(properties.users().stream()
                .map(u -> User.withUsername(u.name())
                        .password(encoder.encode(u.password()))
                        .roles(u.roles().toArray(String[]::new))
                        .build())
                .toList());
    }
}

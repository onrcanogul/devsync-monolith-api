package com.api.devsync.configuration;

import com.api.devsync.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/git/**").authenticated()
//                        .requestMatchers("/api/analyze/**").authenticated()
//                        .requestMatchers("/api/commit/**").authenticated()
//                        .requestMatchers("/api/pull-request/**").authenticated()
//                        .requestMatchers("/api/commit/**").authenticated()
//                        .requestMatchers("/api/repository/**").authenticated()
//                        .requestMatchers("api/git-webhook/**").authenticated()
                        .anyRequest().authenticated()
                ).addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}

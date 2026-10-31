package com.brightloop.config;

import com.brightloop.security.JwtAuthenticationFilter;
import com.brightloop.user.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean UserDetailsService userDetailsService(AppUserRepository users){return email->users.findByEmailIgnoreCase(email).map(u->User.withUsername(u.getEmail()).password(u.getPasswordHash()).authorities("ROLE_"+u.getRole().name()).disabled(!u.isEnabled()).build()).orElseThrow(()->new org.springframework.security.core.userdetails.UsernameNotFoundException("Account not found"));}
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter jwtFilter)throws Exception{
        return http.csrf(csrf->csrf.disable()).cors(cors->cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/auth/register","/api/auth/password/**","/actuator/health","/dev/h2-console/**").permitAll().requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().anyRequest().authenticated())
            .headers(h->h.frameOptions(f->f.sameOrigin())).addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(){var c=new CorsConfiguration();c.setAllowedOriginPatterns(java.util.List.of("http://localhost:5173","http://127.0.0.1:5173"));c.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));var s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}

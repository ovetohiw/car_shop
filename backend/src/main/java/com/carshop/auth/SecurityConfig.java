package com.carshop.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * JWT is the only authentication mechanism; disable Boot's generated default user.
   */
  @Bean
  UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager();
  }


  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      ObjectMapper objectMapper) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/health").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/cars/**", "/api/parts/**").permitAll()
            .requestMatchers("/api/staff/**").hasRole("STAFF")
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, exception) ->
                writeError(response, objectMapper, HttpStatus.UNAUTHORIZED, "Unauthorized"))
            .accessDeniedHandler((request, response, exception) ->
                writeError(response, objectMapper, HttpStatus.FORBIDDEN, "Forbidden")))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:8080"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    configuration.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private static void writeError(
      jakarta.servlet.http.HttpServletResponse response,
      ObjectMapper objectMapper,
      HttpStatus status,
      String message) throws java.io.IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getOutputStream(), Map.of(
        "status", status.value(),
        "error", message));
  }
}

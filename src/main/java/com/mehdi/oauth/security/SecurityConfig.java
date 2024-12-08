package com.mehdi.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(apiConfigurationSource()));
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/private/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                )
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("http://localhost:5173")
                );
        return http.build();
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration google = googleClientRegistration();
        return new InMemoryClientRegistrationRepository(google);
    }

    private ClientRegistration googleClientRegistration() {
        return CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId("463918211853-g9a3b6s65rc5lpv4lu0bu5i61j2167up.apps.googleusercontent.com")
                .clientSecret("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")
                .build();
    }

    UrlBasedCorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

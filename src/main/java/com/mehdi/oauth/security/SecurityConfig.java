package com.mehdi.oauth.security;

import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.security.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    SecurityConfig(OAuth2SuccessHandler oAuth2SuccessHandler,
                   TokenService tokenService,
                   CustomUserDetailsService userDetailsService) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(apiConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(jwtAuthenticationFilter(
                tokenService,
                userDetailsService,
                authenticationManager()), OAuth2LoginAuthenticationFilter.class);
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/private/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout(logout -> logout
                        .deleteCookies("refreshToken", "userInfo")
                        .clearAuthentication(true)
                        .logoutSuccessUrl("http://localhost:4200")
                )
                .oauth2Client(withDefaults());
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
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline")
                .build();
    }

    private UrlBasedCorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager () {
        JWTAuthenticationProvider JWTAuthenticationProvider =
                new JWTAuthenticationProvider();
        ProviderManager providerManager = new ProviderManager(JWTAuthenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }


    public JWTAuthenticationFilter jwtAuthenticationFilter(
            TokenService tokenService,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        return new JWTAuthenticationFilter(tokenService, userDetailsService, authenticationManager);
    }
}

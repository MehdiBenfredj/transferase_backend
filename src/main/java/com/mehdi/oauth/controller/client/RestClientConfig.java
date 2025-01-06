package com.mehdi.oauth.controller.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${spring.application.devtoken}")
    private String token;

    @Bean
    public RestClient authServerRestClient() {
        // Create a custom interceptor that specifically uses the ID token
        ClientHttpRequestInterceptor idTokenInterceptor = (request, body, execution) -> {
            // Extract the ID token
            try {
                OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                OidcIdToken idToken = ((DefaultOidcUser) oauth2Auth.getPrincipal()).getIdToken();
                // Add the ID token as a bearer token
                request.getHeaders().setBearerAuth(idToken.getTokenValue());
            } catch (Exception e) {
                throw new RuntimeException("Failed to extract ID token: ");
            }
            return execution.execute(request, body);
        };

        return RestClient.builder()
                .requestInterceptor(idTokenInterceptor)
                .build();
    }

    @Bean
    public RestClient musicApiRestClient() {
        ClientHttpRequestInterceptor musicApiRestTokenInterceptor = (request, body, execution) -> {
            request.getHeaders().set("Authorization", "DevToken " + token);
            return execution.execute(request, body);
        };

        return RestClient.builder()
                .requestInterceptor(musicApiRestTokenInterceptor)
                .build();
    }

    @Bean
    @Primary
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }


}
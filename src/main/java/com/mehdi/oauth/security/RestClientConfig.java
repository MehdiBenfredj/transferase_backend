package com.mehdi.oauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        // Create a custom interceptor that specifically uses the ID token
        ClientHttpRequestInterceptor idTokenInterceptor = (request, body, execution) -> {
            // Extract the ID token

            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

                OidcIdToken idToken = ((DefaultOidcUser) oauth2Auth.getPrincipal()).getIdToken();
                // Add the ID token as a bearer token
                request.getHeaders().setBearerAuth(idToken.getTokenValue());
            } catch (Exception e) {
                //throw new RuntimeException("Failed to extract ID token: ");
            }
            return execution.execute(request, body);
        };


        return RestClient.builder()
                //.requestInterceptor(idTokenInterceptor)
                .build();
    }

}
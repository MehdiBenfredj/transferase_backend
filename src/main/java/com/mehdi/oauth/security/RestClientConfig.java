package com.mehdi.oauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
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
        //OAuth2ClientHttpRequestInterceptor requestInterceptor =
        //        new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
// Create a custom interceptor that specifically uses the ID token
        ClientHttpRequestInterceptor idTokenInterceptor = (request, body, execution) -> {
            //OAuth2AuthorizedClient authorizedClient = authorizedClientManager
            //        .authorize(OAuth2AuthorizeRequest
            //                .withClientRegistrationId("google")
            //                .principal(SecurityContextHolder.getContext().getAuthentication())
            //                .build());


                // Extract the ID token
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            OidcIdToken idToken = ((DefaultOidcUser) oauth2Auth.getPrincipal()).getIdToken();
                // Add the ID token as a bearer token
            request.getHeaders().setBearerAuth(idToken.getTokenValue());


            return execution.execute(request, body);
        };


        return RestClient.builder()
                .requestInterceptor(idTokenInterceptor)
                .build();
    }

}
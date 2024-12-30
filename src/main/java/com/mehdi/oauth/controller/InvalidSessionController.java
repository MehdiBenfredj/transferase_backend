package com.mehdi.oauth.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.RefreshTokenAuthenticationToken;
import com.mehdi.oauth.service.CustomUserDetailsService;
import com.mehdi.oauth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;


@RequestMapping("/public")
@RestController
public class InvalidSessionController {

    private final RestClient restClient;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public InvalidSessionController(RestClient restClient, RefreshTokenService refreshTokenService, CustomUserDetailsService customUserDetailsService, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.restClient = restClient;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping(value = "/invalidSession")
    public ResponseEntity<Map<String, String>> invalidSession() {
        Map<String, String> map = new HashMap<>();
        map.put("redirectUrl", "refreshToken");
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/refreshToken",consumes = { "text/plain"})
    public ResponseEntity refreshToken(@RequestBody String refreshToken, HttpServletRequest request) {

        try {
            DecodedJWT decodedJWT = refreshTokenService.verifyRefreshToken(refreshToken);
            String userEmail = decodedJWT.getSubject();
            User user = userDetailsService.loadUserByEmail(userEmail);
            Authentication authentication = new RefreshTokenAuthenticationToken(user, decodedJWT);
            Authentication authResponse = authenticationManager.authenticate(authentication);
            // Establish the security context
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authResponse);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            String newRefreshToken = refreshTokenService.createRefreshToken(userEmail);
            return ResponseEntity.ok(Map.of("refreshToken", newRefreshToken));
        } catch (TokenExpiredException tokenExpiredException) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token is expired"));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<List<AppController.Message>> messages() {
        AppController.Message[] messages = this.restClient.get()
                .uri("http://localhost:8081/messages")
                .attributes(clientRegistrationId("google"))
                .retrieve()
                .body(AppController.Message[].class);
        return ResponseEntity.ok(Arrays.asList(messages));
    }
}

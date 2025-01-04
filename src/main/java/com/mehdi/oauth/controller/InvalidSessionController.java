package com.mehdi.oauth.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.RefreshTokenAuthenticationToken;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RequestMapping("/public")
@RestController
public class InvalidSessionController {

    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public InvalidSessionController(
            RefreshTokenService refreshTokenService,
            CustomUserDetailsService customUserDetailsService,
            AuthenticationManager authenticationManager) {
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping(value = "/invalidSession")
    public ResponseEntity invalidSession(HttpServletRequest request) {
        OkHttpClient client = new OkHttpClient();

        Request refreshTokenRequest = new Request.Builder()
                .url("http://localhost:4200/refreshToken")
                .build();

        try (Response response = client.newCall(refreshTokenRequest).execute()) {
            String refreshToken = response.body().string();
            JSONObject jsonObject = new JSONObject(refreshToken);
            refreshToken = jsonObject.getString("refreshToken");
            return refreshToken(refreshToken, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/refreshToken", consumes = "text/plain")
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
}

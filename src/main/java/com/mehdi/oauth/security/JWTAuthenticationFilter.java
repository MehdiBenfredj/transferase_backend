package com.mehdi.oauth.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.security.service.TokenService;
import com.mehdi.oauth.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;


    JWTAuthenticationFilter(
            TokenService tokenService,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        String token = getJwtFromRequest(request);
        if (token != null && !token.isEmpty()) {
            try {
                DecodedJWT decodedJWT = tokenService.verifyRefreshToken(token);
                String userEmail = decodedJWT.getSubject();
                User user = userDetailsService.loadUserByEmail(userEmail);
                Authentication authentication = new JWTAuthenticationToken(user, decodedJWT);
                Authentication authResponse = authenticationManager.authenticate(authentication);

                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authResponse);
                if (decodedJWT.getClaim("is_refresh").asBoolean()) {
                    Cookie refreshTokenCookie = CookieUtils.createCookie("refreshToken", tokenService.createRefreshToken(
                            user.getEmail(),
                            user.getUsername(),
                            user.getPhotoUrl()));
                    Cookie authTokenCookie = CookieUtils.createCookie("authToken", tokenService.createAuthToken(
                            user.getEmail(),
                            user.getUsername(),
                            user.getPhotoUrl()));
                    response.addCookie(refreshTokenCookie);
                    response.addCookie(authTokenCookie);
                }
                // Optionally add new refresh token to the response if needed
            } catch (TokenExpiredException ex) {
                //Handle expired token explicitly
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired. Please provide a refresh token.\"}");
                return;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);

    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken!=null &&  bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}

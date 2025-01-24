package com.mehdi.oauth.security;

import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.security.service.TokenService;
import com.mehdi.oauth.utils.CookieUtils;
import com.mehdi.oauth.utils.JSONParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;

    public OAuth2SuccessHandler(CustomUserDetailsService customUserDetailsService,
                                TokenService tokenService) {
        this.customUserDetailsService = customUserDetailsService;
        this.tokenService = tokenService;
    }


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;

        OAuth2User principal = oauth2Auth.getPrincipal();
        User user = customUserDetailsService.loadUserByEmail(principal.getAttribute("email"));

        if (user == null) {
            user = customUserDetailsService.createUser(
                    principal.getAttribute("email"),
                    principal.getAttribute("name"),
                    principal.getAttribute("picture")                                                  );
        }

        // Send user info in a cookie
        String encodedCookie = URLEncoder.encode(JSONParser.userToJsonString(user),
                StandardCharsets.UTF_8);
        Cookie userInfoCookie = CookieUtils.createCookie("userInfo", encodedCookie);
        Cookie refreshTokenCookie = CookieUtils.createCookie("refreshToken", tokenService.createRefreshToken(
                user.getEmail(),
                user.getUsername(),
                user.getPhotoUrl()));
        Cookie authTokenCookie = CookieUtils.createCookie("authToken", tokenService.createAuthToken(
                user.getEmail(),
                user.getUsername(),
                user.getPhotoUrl()));

        // Add the cookie to the HTTP response
        response.addCookie(userInfoCookie);
        response.addCookie(refreshTokenCookie);
        response.addCookie(authTokenCookie);

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:4200");
        super.onAuthenticationSuccess(request, response, authentication);
    }

}

package com.mehdi.oauth.controller;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.JWTAuthenticationToken;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.utils.JSONParser;
import com.mehdi.oauth.service.SubscriptionsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;

@RestController
public class SubscriptionsController {
    private final SubscriptionsService subscriptionsService;
    private final CustomUserDetailsService userDetailsService;

    SubscriptionsController(SubscriptionsService subscriptionsService, CustomUserDetailsService userDetailsService) {
        this.subscriptionsService = subscriptionsService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("create_subscription")
    public RedirectView collectDataFromReturnUrl(@RequestParam("data64") String data64, Authentication authentication) {
        String userId;
        if (authentication instanceof OAuth2AuthenticationToken token) {
            String email = token.getPrincipal().getAttribute("email");
            User user = userDetailsService.loadUserByEmail(email);
            userId = user.getId();
        } else {
            JWTAuthenticationToken token = (JWTAuthenticationToken) authentication;
            userId = token.getPrincipal().getId();
        }

        if (data64 != null && !data64.isEmpty()) {
            String decodedData = new String(Base64.getDecoder().decode(data64));
            Subscription subscription = JSONParser.toSubscription(decodedData, userId);
            subscriptionsService.createSubscription(subscription);
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:4200");
        return redirectView;
    }
}

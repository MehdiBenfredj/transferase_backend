package com.mehdi.oauth.controller;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.model.User;
import com.mehdi.oauth.security.JWTAuthenticationToken;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.utils.JSONParser;
import com.mehdi.oauth.service.SubscriptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
public class SubscriptionsController {
    private final SubscriptionsService subscriptionsService;
    private final CustomUserDetailsService userDetailsService;

    SubscriptionsController(SubscriptionsService subscriptionsService, CustomUserDetailsService userDetailsService) {
        this.subscriptionsService = subscriptionsService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "create_subscription", consumes = "application/json")
    public ResponseEntity<String> collectDataFromReturnUrl(Authentication authentication, @RequestBody String data) {
        String userId;
        if (authentication instanceof OAuth2AuthenticationToken token) {
            String email = token.getPrincipal().getAttribute("email");
            User user = userDetailsService.loadUserByEmail(email);
            userId = user.getId();
        } else {
            JWTAuthenticationToken token = (JWTAuthenticationToken) authentication;
            userId = token.getPrincipal().getId();
        }

        if (data != null && !data.isEmpty()) {
            data = JSONParser.extractData(data);
            String decodedData = new String(Base64.getDecoder().decode(data));
            Subscription subscription = JSONParser.toSubscription(decodedData, userId);
            subscriptionsService.createSubscription(subscription);
        }
        return ResponseEntity.ok("Subscription created successfully");
    }
}

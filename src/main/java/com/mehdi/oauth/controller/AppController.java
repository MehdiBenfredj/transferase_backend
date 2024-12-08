package com.mehdi.oauth.controller;

import jakarta.servlet.Filter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AppController {

    private final Filter springSecurityFilterChain;

    public AppController(Filter springSecurityFilterChain) {
        this.springSecurityFilterChain = springSecurityFilterChain;
    }

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "This is a private page";
    }

    @GetMapping("/user_info")
    public String userInfo(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttribute("email");
    }
}

package com.mehdi.oauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AppController {

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

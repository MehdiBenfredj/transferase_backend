package com.mehdi.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;


@RestController
public class AppController {

    private final RestClient restClient;

    public AppController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page";
    }

    @GetMapping("/private")
    public ResponseEntity<Map<String,String>> privatePage() {
        Map map = Map.of("message", "This is a private page");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/user_info")
    public String userInfo(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttribute("email");
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> messages() {
        Message[] messages = this.restClient.get()
                .uri("http://localhost:8081/messages")
                .attributes(clientRegistrationId("google"))
                .retrieve()
                .body(Message[].class);
        return ResponseEntity.ok(Arrays.asList(messages));
    }

    public record Message(String message) {
    }
}

package com.mehdi.oauth.controller;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.service.MusicAPIService;
import com.mehdi.oauth.service.SubscriptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("music-api")
public class MusicAPIController {
    private final RestClient restClient;
    private final MusicAPIService musicAPIService;
    private CustomUserDetailsService userService;
    private SubscriptionsService subscriptionsService;

    public MusicAPIController(RestClient restClient, CustomUserDetailsService userService, SubscriptionsService subscriptionsService, MusicAPIService musicAPIService) {
        this.restClient = restClient;
        this.userService = userService;
        this.subscriptionsService = subscriptionsService;
        this.musicAPIService = musicAPIService;
    }

    @GetMapping("user-subscriptions")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@RequestParam("email") String email) {

        String userId = userService.loadUserByEmail(email).getId();
        List<Subscription> subscriptions = subscriptionsService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("user-service-playlists")
    public ResponseEntity<List> getUserServicePlaylists(
            @RequestParam("email") String email,
            @RequestParam("service") String service) {

        List playlists = musicAPIService.getUserServicePlaylists(email, service);
        return ResponseEntity.ok(playlists);
    }
}

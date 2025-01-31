package com.mehdi.oauth.controller;

import com.mehdi.oauth.model.Subscription;
import com.mehdi.oauth.model.WorkflowDTO;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.service.MusicAPIService;
import com.mehdi.oauth.service.SubscriptionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("music-api")
public class MusicAPIController {
    private final MusicAPIService musicAPIService;
    private final CustomUserDetailsService userService;
    private final SubscriptionsService subscriptionsService;

    public MusicAPIController(CustomUserDetailsService userService, SubscriptionsService subscriptionsService, MusicAPIService musicAPIService) {
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

    @PostMapping(path = "transfer", consumes = "application/json")
    public ResponseEntity transfer(@RequestBody WorkflowDTO workflow) {
        musicAPIService.transfer(workflow);
        return ResponseEntity.ok("Transfer complete");
    }
    
    @DeleteMapping("delete")
    public ResponseEntity deleteSubscription(@RequestParam String integrationUserUUID) {
        musicAPIService.deleteSubscription(integrationUserUUID);
        return ResponseEntity.ok("Subscription deleted");
    }
}

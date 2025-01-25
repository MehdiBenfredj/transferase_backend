package com.mehdi.oauth.service;

import com.mehdi.oauth.security.service.CustomUserDetailsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class MusicAPIService {
    private final RestClient musicRestClient;
    private final CustomUserDetailsService userService;
    private final SubscriptionsService subscriptionsService;

    public MusicAPIService(@Qualifier("musicApiRestClient") RestClient restClient, CustomUserDetailsService userService, SubscriptionsService subscriptionsService) {
        this.musicRestClient = restClient;
        this.userService = userService;
        this.subscriptionsService = subscriptionsService;
    }

    public List<Object> getUserServicePlaylists(String email, String service) {
        try {
            String musicApiUuid = getUserMusicApiUuid(email, service);
            String result = musicRestClient.get()
                    .uri("https://api.musicapi.com/api/"+ musicApiUuid +"/playlists")
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            System.out.println(result);
            JSONObject jsonObject = new JSONObject(result);
            return new JSONArray(jsonObject.getJSONArray("results")).toList();
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public String getUserMusicApiUuid(String email, String service) {
        String userId = userService.loadUserByEmail(email).getId();
        try {
            return subscriptionsService.getSubscriptionsByUserIdAndService(userId, service).getIntegrationUserUUID();
        } catch (Exception e) {
            throw  new RuntimeException("Could not get user music api uuid for email " + email);
        }
    }


}

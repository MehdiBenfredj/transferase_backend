package com.mehdi.oauth.service;

import com.mehdi.oauth.model.*;
import com.mehdi.oauth.security.JWTAuthenticationToken;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class MusicAPIService {
    private static final Logger logger = LoggerFactory.getLogger(MusicAPIService.class);
    private final String BASE_URL = "https://api.musicapi.com/api/";
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
                    .uri(BASE_URL + musicApiUuid +"/playlists")
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

    public void transfer(WorkflowDTO workflow) {
        String originUuid;
        String email;
        String destUuid;
        // get infos
        try {
            JWTAuthenticationToken auth = (JWTAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            email = auth.getPrincipal().getEmail();
            originUuid = getUserMusicApiUuid(email, workflow.getWorkflow().getOrigin().getService());
            destUuid = getUserMusicApiUuid(email, workflow.getWorkflow().getDestination());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
        // for each playlist
        for (WorkflowDTO.Workflow.Playlist originPlaylist : workflow.getWorkflow().getSelectedPlaylists()) {
            // get songs from playlist
            PlaylistItemsDto result = getPlaylistItems(originUuid, originPlaylist.getId());
            // create dest playlist
            WorkflowDTO.Workflow.Playlist destPlaylist = createPlaylist(destUuid, originPlaylist);
            // for each song
            if (result != null) {
                List<String> tracksToAdd = new ArrayList<>();
                for (Track track : result.getTracks()) {
                    // add to dest playlist
                    Track destTrack = getDestTrack(destUuid, workflow.getWorkflow().getDestination(), track);
                    if (destTrack != null) {
                        tracksToAdd.add(destTrack.getId());
                    }
                }
                addTracksToPlaylist(destUuid, destPlaylist.getId(), tracksToAdd);
            }

        }


    }

    private Track getDestTrack(String userUUID, String service, Track track) {
        SearchResultDto searchResult = new SearchResultDto();
        if (track.getIsrc() != null) {
            try {
                 searchResult = musicRestClient.post()
                        .uri(BASE_URL + userUUID +"/search")
                        .accept(APPLICATION_JSON)
                         .body(new IsrcSearchDto("track", track.getIsrc()))
                        .retrieve()
                        .body(SearchResultDto.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return searchResult.getResults().getFirst().getTrack();
            }
        } else {
            try {
                SearchDto body = SearchDto.builder()
                        .type("track")
                        .track(track.getName())
                        .artist(track.getArtists() != null ? track.getArtists().getFirst().getName(): null)
                        .album(track.getAlbum() != null ? track.getAlbum().getName() : null)
                        .build();
                searchResult = musicRestClient.post()
                        .uri(BASE_URL + userUUID +"/search")
                        .accept(APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(SearchResultDto.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return searchResult.getResults().getFirst().getTrack();
            }
        }

        return searchResult != null ? searchResult.getResults().getFirst().getTrack() : null ;
    }

    private boolean addTracksToPlaylist(String uuid, String playlistId, List<String> itemIds) {

        try {
             musicRestClient.post()
                    .uri(BASE_URL + uuid +"/playlists/" + playlistId + "/items")
                    .accept(APPLICATION_JSON)
                    .body(new ItemIdsDto(itemIds))
                    .retrieve()
                    .body(WorkflowDTO.Workflow.Playlist.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private WorkflowDTO.Workflow.Playlist createPlaylist(String uuid, WorkflowDTO.Workflow.Playlist origin) {
        WorkflowDTO.Workflow.Playlist playlist = new WorkflowDTO.Workflow.Playlist();
        try {
            CreatePlaylistDto body = CreatePlaylistDto.builder()
                    .name(origin.getName())
                    .description(!origin.getDescription().isEmpty() ? origin.getDescription() : null)
                    .visibility(origin.getVisibility())
                    .build();
            playlist = musicRestClient.post()
                    .uri(BASE_URL + uuid +"/playlists/")
                    .accept(APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(WorkflowDTO.Workflow.Playlist.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return playlist;
        }
        return  playlist;
    }

    private PlaylistItemsDto getPlaylistItems(String uuid, String playlistId) {
        PlaylistItemsDto playlistItems;
        try {
            playlistItems = musicRestClient.get()
                    .uri(BASE_URL + uuid +"/playlists/" + playlistId + "/items")
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(PlaylistItemsDto.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return playlistItems;
    }
}

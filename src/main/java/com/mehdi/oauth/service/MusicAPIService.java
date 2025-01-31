package com.mehdi.oauth.service;

import com.mehdi.oauth.model.*;
import com.mehdi.oauth.repository.SubscriptionsRepository;
import com.mehdi.oauth.security.JWTAuthenticationToken;
import com.mehdi.oauth.security.service.CustomUserDetailsService;
import com.mehdi.oauth.utils.StringUtils;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
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
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class MusicAPIService {
    private final List<String> SUPPORT_ISRC_SEARCH = List.of("spotify");
    private static final Logger logger = LoggerFactory.getLogger(MusicAPIService.class);
    private final String BASE_URL = "https://api.musicapi.com/api/";
    private final RestClient musicRestClient;
    private final CustomUserDetailsService userService;
    private final SubscriptionsService subscriptionsService;
    private final SubscriptionsRepository subscriptionsRepository;

    public MusicAPIService(@Qualifier("musicApiRestClient") RestClient restClient, CustomUserDetailsService userService, SubscriptionsService subscriptionsService, SubscriptionsRepository subscriptionsRepository) {
        this.musicRestClient = restClient;
        this.userService = userService;
        this.subscriptionsService = subscriptionsService;
        this.subscriptionsRepository = subscriptionsRepository;
    }

    public List<Object> getUserServicePlaylists(String email, String service) {
        try {
            String musicApiUuid = getUserMusicApiUuid(email, service);
            String result = musicRestClient.get()
                    .uri(BASE_URL + musicApiUuid +"/playlists")
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
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

        for (WorkflowDTO.Workflow.Playlist originPlaylist : workflow.getWorkflow().getSelectedPlaylists()) {
            // Create destination playlist for each origin playlist
            WorkflowDTO.Workflow.Playlist destPlaylist = createPlaylist(destUuid, originPlaylist);
            String nextPageToken = null;

            do {
                List<String> tracksToAdd = new ArrayList<>();
                PlaylistItemsDto currentPage = getPlaylistItems(
                        originUuid,
                        originPlaylist.getId(),
                        nextPageToken
                );

                if (currentPage == null) break;

                // Process tracks in current page
                for (Track track : currentPage.getTracks()) {
                    Optional<Track> destTrack = getDestTrack(
                            destUuid,
                            track,
                            workflow.getWorkflow().getDestination()
                    );
                    destTrack.ifPresent(t -> tracksToAdd.add(t.getId()));
                }

                // Add current page's tracks immediately
                if (!tracksToAdd.isEmpty()) {
                    addTracksToPlaylist(destUuid, destPlaylist.getId(), tracksToAdd);
                }

                // Update pagination token
                nextPageToken = currentPage.getNextParam();
            } while (nextPageToken != null);
        }
    }


    private Optional<Track> getDestTrack(String userUUID, Track track, String destService) {
        Optional<Track> destTrack = Optional.empty();
        SearchResultDto searchResult = new SearchResultDto();
        if (track.getIsrc() != null && SUPPORT_ISRC_SEARCH.contains(destService)) {
            try {
                searchResult = musicRestClient.post()
                        .uri(BASE_URL + userUUID + "/search")
                        .accept(APPLICATION_JSON)
                        .body(new IsrcSearchDto("track", track.getIsrc()))
                        .retrieve()
                        .body(SearchResultDto.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if(searchResult != null) {
                destTrack = Optional.of(searchResult)
                        .map(SearchResultDto::getResults)
                        .filter(results -> !results.isEmpty())
                        .map(List::getFirst)
                        .map(SearchResultDto.ResultDTO::getTrack);
            }
        } else {
            try {
                SearchDto body = SearchDto.builder()
                        .type("track")
                        .track(StringUtils.trimAfterDelimiters(track.getName()).toLowerCase())
                        .artist(track.getArtists() != null ? StringUtils.trimAfterDelimiters(track.getArtists().getFirst().getName()).toLowerCase() : null)
                        .album(track.getAlbum() != null ? StringUtils.trimAfterDelimiters(track.getAlbum().getName()).toLowerCase() : null)
                        .build();
                searchResult = musicRestClient.post()
                        .uri(BASE_URL + userUUID + "/search")
                        .accept(APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(SearchResultDto.class);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return Optional.ofNullable(searchResult.getResults().getFirst().getTrack());
            }

            if (searchResult != null) {
                BoundExtractedResult<SearchResultDto.ResultDTO> match = FuzzySearch.extractOne(
                        track.getName(),
                        searchResult.getResults(),
                        result -> result.getTrack().getName()
                );
                if (match.getScore() < 90 && track.getAlbum() != null) {
                    track.setAlbum(null);
                    destTrack = getDestTrack(userUUID, track, destService);
                } else {
                    SearchResultDto.ResultDTO result = match.getReferent();
                    destTrack = Optional.ofNullable(result.getTrack());
                }
            }
        }

        return destTrack;
    }

    private void addTracksToPlaylist(String uuid, String playlistId, List<String> itemIds) {

        try {
             musicRestClient.post()
                    .uri(BASE_URL + uuid +"/playlists/" + playlistId + "/items")
                    .accept(APPLICATION_JSON)
                    .body(new ItemIdsDto(itemIds))
                    .retrieve()
                    .body(WorkflowDTO.Workflow.Playlist.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    private PlaylistItemsDto getPlaylistItems(String uuid, String playlistId, String nextParam) {
        PlaylistItemsDto playlistItems;
        try {
            String next = nextParam != null ? "?nextParam=" + nextParam : "";
            String url = BASE_URL + uuid +"/playlists/" + playlistId + "/items" + next;
            playlistItems = musicRestClient.get()
                    .uri(url)
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(PlaylistItemsDto.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return playlistItems;
    }

    public void deleteSubscription(String integrationUserUUID) {
        try {
            Subscription subscriptionToDelete =
                    subscriptionsRepository.findSubscriptionByIntegrationUserUUID(integrationUserUUID)
                                    .orElseThrow(() ->
                                            new RuntimeException("Subscription " + integrationUserUUID + " not found"));
            subscriptionsRepository.delete(subscriptionToDelete);
        } catch (Exception exception) {
            logger.error("Could not delete integrationUserUUID {}", integrationUserUUID, exception);
        }
    }
}

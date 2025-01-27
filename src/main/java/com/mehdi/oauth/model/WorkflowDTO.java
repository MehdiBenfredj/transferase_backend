package com.mehdi.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WorkflowDTO {
    @JsonProperty("workflow")
    private Workflow workflow;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Workflow {
        private Origin origin;
        private String destination;
        @JsonProperty("selected_playlists")
        private List<Playlist> selectedPlaylists;

        @Setter
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @ToString
        public static class Origin {
            private String service;
            private List<Playlist> playlists;

        }

        @Setter
        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @ToString
        public static class Playlist {
            @JsonProperty("totalItems")
            private int totalItems;
            private Integer followers;
            private String visibility;
            @JsonProperty("isOwner")
            private boolean owner;
            @JsonProperty("imageUrl")
            private String imageUrl;
            private String name;
            @JsonProperty("publicUrl")
            private String publicUrl;
            private String description;
            private String id;
            private boolean selected;

        }
    }
}
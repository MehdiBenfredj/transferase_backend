package com.mehdi.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistItemsDto {
    @JsonProperty("results")
    private List<Track> tracks;
    private int totalItems;
    private String nextParam;
}


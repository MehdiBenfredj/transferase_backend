package com.mehdi.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Track {
    // Getters and Setters
    private String id;
    private String entryId;
    private String name;
    private String imageUrl;
    private String previewUrl;
    private String isrc;
    private String duration;
    private String publicUrl;
    private Album album;
    private List<Artist> artists;
}

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
public class Album {
    private String id;
    private String entryId;
    private String name;
    private String imageUrl;
    private int totalItems;
    private String publicUrl;
    private Integer followers;
    private List<Artist> artists;
}

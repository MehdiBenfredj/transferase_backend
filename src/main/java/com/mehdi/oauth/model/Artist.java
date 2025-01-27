package com.mehdi.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Artist {
    private String id;
    private String entryId;
    private String name;
    private String imageUrl;
    private Integer followers;
    private String publicUrl;
}

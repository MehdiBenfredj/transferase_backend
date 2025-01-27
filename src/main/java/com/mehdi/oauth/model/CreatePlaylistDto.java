package com.mehdi.oauth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePlaylistDto {
    String name;
    String description;
    String visibility;
}

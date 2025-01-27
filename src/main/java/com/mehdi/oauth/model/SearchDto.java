package com.mehdi.oauth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields during JSON serialization
public class SearchDto {
    private String type;
    private String track;
    private String artist;
    private String album;
}
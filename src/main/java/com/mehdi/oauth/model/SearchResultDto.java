package com.mehdi.oauth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private List<ResultDTO> results;
    private int totalItems;
    private String nextParam;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDTO {
        private String id;
        private String type;
        private Track track;
    }

}

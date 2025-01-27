package com.mehdi.oauth.model;

import java.util.Collections;
import java.util.List;

public record ItemIdsDto(List<String> itemIds) {
    public ItemIdsDto {
        itemIds = itemIds != null ? Collections.unmodifiableList(itemIds) : Collections.emptyList();
    }
}
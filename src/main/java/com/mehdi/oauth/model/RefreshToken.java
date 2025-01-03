package com.mehdi.oauth.model;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public final class RefreshToken {
    private int id;
    private String token;
    private Instant expiryDate;
}

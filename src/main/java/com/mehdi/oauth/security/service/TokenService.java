package com.mehdi.oauth.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenService {
    Algorithm algorithm = Algorithm.HMAC256("secret");

    public String createAuthToken(String email, String userName, String photoUrl) {
        String authToken = JWT.create().withSubject(email)
                .withClaim("is_refresh", false)
                .withClaim("userName", userName)
                .withClaim("photoUrl", photoUrl)
                .withExpiresAt(Instant.now().plusMillis(600000 /* 10minutes */))
                .sign(algorithm);
        return authToken;
    }

    public String createRefreshToken(String email, String userName, String photoUrl){
        String refreshToken = JWT.create().withSubject(email)
                .withClaim("is_refresh", true)
                .withClaim("userName", userName)
                .withClaim("photoUrl", photoUrl)
                .withExpiresAt(Instant.now().plusMillis(600000 * 6 * 24 * 14))
                .sign(algorithm);
        return refreshToken;
    }

    public DecodedJWT verifyRefreshToken(String token){
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt;
    }
}

package com.mehdi.oauth.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenService {
    Algorithm algorithm = Algorithm.HMAC256("secret");

    public String createRefreshToken(String email){
        String refreshToken = JWT.create().withSubject(email)
                .withExpiresAt(Instant.now().plusMillis(600000 /* 10minutes */ * 6 * 24 * 14))
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

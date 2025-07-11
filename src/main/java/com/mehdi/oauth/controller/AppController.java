package com.mehdi.oauth.controller;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.Map;

import static com.mehdi.oauth.utils.KeyUtils.loadPrivateKey;

@RestController
public class AppController {

    @GetMapping("/music_api_token")
    public ResponseEntity<Map<String, String>> musicApiToken() throws Exception {
        // Load private key
        PrivateKey privateKey = loadPrivateKey("src/main/resources/private_pkcs8.key");

        // Create a signer using the private key
        JWSSigner signer = new ECDSASigner((ECPrivateKey) privateKey);

        // Prepare the JWT claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("939d7de6-ae69-4e0e-a36c-ee9c92b7a5c5")
                .subject("6a736625-71ba-4ab4-9dd7-ca674038c17c")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 1209600000)) // 14 days expiry
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)  // Set the type to JWT
                .keyID("2ddeb1cb39")   // Optional: specify a key ID (kid)
                .build();
        // Create the signed JWT
        SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet
        );

        // Sign the JWT
        signedJWT.sign(signer);

        // Serialize the JWT to a compact form
        String token = signedJWT.serialize();

        return ResponseEntity.ok(Map.of("DevToken", token));
    }

    @GetMapping("/private")
    public ResponseEntity<Map<String,String>> privatePage() {
        Map map = Map.of("message", "This is a private page");
        return ResponseEntity.ok(map);
    }
}

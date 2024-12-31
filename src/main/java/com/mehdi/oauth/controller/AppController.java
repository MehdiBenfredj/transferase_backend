package com.mehdi.oauth.controller;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mehdi.oauth.utils.KeyUtils.loadPrivateKey;
import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@RestController
public class AppController {

    private final RestClient restClient;

    public AppController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/music_api_token")
    public String publicPage() throws Exception {
        // Load private key
        PrivateKey privateKey = loadPrivateKey("src/main/resources/private_pkcs8.key");

        // Create a signer using the private key
        JWSSigner signer = new ECDSASigner((ECPrivateKey) privateKey);

        // header


        // Prepare the JWT claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("939d7de6-ae69-4e0e-a36c-ee9c92b7a5c5")
                .subject("6a736625-71ba-4ab4-9dd7-ca674038c17c")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 1209600000)) // 14 days expiry
                .build();


        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)  // Set the type to JWT
                .keyID("2ddeb1cb39")      // Optional: specify a key ID (kid)
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

        System.out.println("Generated JWT: " + token);
        return "This is a public page";
    }

    @GetMapping("/private")
    public ResponseEntity<Map<String,String>> privatePage() {
        Map map = Map.of("message", "This is a private page");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/user_info")
    public String userInfo(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttribute("email");
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> messages() {
        Message[] messages = this.restClient.get()
                .uri("http://localhost:8081/messages")
                .attributes(clientRegistrationId("google"))
                .retrieve()
                .body(Message[].class);
        return ResponseEntity.ok(Arrays.asList(messages));
    }

    public record Message(String message) {
    }
}

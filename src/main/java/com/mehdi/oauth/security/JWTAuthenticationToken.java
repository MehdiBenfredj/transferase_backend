package com.mehdi.oauth.security;

import com.mehdi.oauth.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    Object credentials;
    User principal;

    public JWTAuthenticationToken(User principal, Object credentials) {
        super(null);
        this.credentials = credentials;
        this.principal = principal;
        setAuthenticated(false);
    }

    public void authenticated() {
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public User getPrincipal() {
        return this.principal;
    }
}

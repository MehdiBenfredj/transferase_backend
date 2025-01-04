package com.mehdi.oauth.security;

import com.mehdi.oauth.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {
    Object credentials;
    User principal;

    public RefreshTokenAuthenticationToken(User principal, Object credentials) {
        super(null);
        this.credentials = credentials;
        this.principal = principal;
        setAuthenticated(false);
    }

    public void authenticated(
            User principal,
            Object credentials,
            Collection<? extends GrantedAuthority> grantedAuthorities) {
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

package com.mehdi.oauth.security;

import com.mehdi.oauth.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected final Log logger = LogFactory.getLog(getClass());


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(RefreshTokenAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("RefreshTokenAuthenticationProvider.onlySupports",
                        "Only RefreshTokenAuthenticationToken is supported"));
        User user = (User) authentication.getPrincipal();

        return createSuccessAuthentication(user, authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }

    protected Authentication createSuccessAuthentication(
            User principal,
            Authentication authentication) {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        RefreshTokenAuthenticationToken result = new RefreshTokenAuthenticationToken(principal, authentication);
        result.authenticated(
                principal,
                authentication.getCredentials(),
                null);
        result.setDetails(authentication.getDetails());
        this.logger.debug("Authenticated user");
        return result;
    }
}

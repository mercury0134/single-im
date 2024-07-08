package org.mercury.im.gateway.core.secuirty;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtTokenResolver jwtTokenResolver;

    public JwtAuthenticationProvider(JwtTokenResolver jwtTokenResolver) {
        this.jwtTokenResolver = jwtTokenResolver;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication,
                "JwtAuthenticationProvider.onlySupports Only JwtAuthenticationToken is supported");

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        UserDetails userDetails = this.jwtTokenResolver.resolve(jwtAuthenticationToken.getPrincipal().toString());

        if (userDetails.getAuthorities() != null) {
            return new JwtAuthenticationToken(userDetails, true, userDetails.getAuthorities());
        } else {
            return new JwtAuthenticationToken(userDetails, true);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}

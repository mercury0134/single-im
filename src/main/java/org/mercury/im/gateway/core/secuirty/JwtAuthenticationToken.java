package org.mercury.im.gateway.core.secuirty;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serial;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    public JwtAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal, boolean authenticated) {
        super(null);
        this.principal = principal;
        setAuthenticated(authenticated);
    }

    public JwtAuthenticationToken(Object principal, boolean authenticated, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(authenticated);
    }



    @Override
    public Object getCredentials() {
        return this.principal;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }


    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}


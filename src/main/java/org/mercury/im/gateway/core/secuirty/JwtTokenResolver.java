package org.mercury.im.gateway.core.secuirty;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenResolver {

    UserDetails resolve(String token);

}
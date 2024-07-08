package org.mercury.im.gateway.core.secuirty;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtil {

    public static Long userId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserBo) authentication.getPrincipal()).getUserId();
    }

}

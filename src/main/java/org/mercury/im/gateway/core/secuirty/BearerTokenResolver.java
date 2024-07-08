package org.mercury.im.gateway.core.secuirty;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface BearerTokenResolver {

    String resolve(HttpServletRequest request);

}


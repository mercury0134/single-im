package org.mercury.im.gateway.core.secuirty;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

/**
 * Implementation of {@link AuthenticationDetailsSource} which builds the details object
 * from an <tt>HttpServletRequest</tt> object, creating a {@code WebAuthenticationDetails}
 * .
 */
public class WebAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    /**
     * @param context the {@code HttpServletRequest} object.
     * @return the {@code WebAuthenticationDetails} containing information about the
     * current request
     */
    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new WebAuthenticationDetails(context);
    }

}

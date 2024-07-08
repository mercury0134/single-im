package org.mercury.im.gateway.core.secuirty;

import jakarta.servlet.http.HttpServletRequest;

public final class ExactUrlRequestMatcher implements RequestMatcher {
    private final String processUrl;

    public ExactUrlRequestMatcher(String processUrl) {
        this.processUrl = processUrl;
    }


    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            uri += "?" + query;
        }
        if ("".equals(request.getContextPath())) {
            return uri.equals(this.processUrl);
        }
        return uri.equals(request.getContextPath() + this.processUrl);
    }

    @Override
    public String toString() {
        return "ExactUrl [processUrl='" + this.processUrl + "']";
    }

}



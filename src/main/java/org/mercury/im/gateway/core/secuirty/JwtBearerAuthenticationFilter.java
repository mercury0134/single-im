package org.mercury.im.gateway.core.secuirty;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mercury.im.gateway.core.secuirty.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtBearerAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

    private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    private final ConfigAttribute permitAll = new SecurityConfig("permitAll");

    public JwtBearerAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManagerResolver = (request) -> authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = this.bearerTokenResolver.resolve(request);

            if (StrUtil.isBlank(token)) {
                throw new AuthenticationException("Did not process request since did not find bearer token");
            }

            JwtAuthenticationToken authenticationRequest = new JwtAuthenticationToken(token);
            authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

            AuthenticationManager authenticationManager = this.authenticationManagerResolver.resolve(request);
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            AuthenticationHandler.loginAuthenticationFailureHandler(request, response, e);
        }
    }
}

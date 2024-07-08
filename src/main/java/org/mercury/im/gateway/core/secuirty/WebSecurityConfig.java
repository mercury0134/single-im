package org.mercury.im.gateway.core.secuirty;

import jakarta.annotation.Resource;
import org.mercury.im.gateway.core.secuirty.exception.AuthenticationException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebMvc
public class WebSecurityConfig implements WebMvcConfigurer {

    @Resource
    private JwtDecoder jwtDecoder;

    @Bean
    public JwtTokenResolver jwtTokenResolver() {
        return token -> {
            Jwt decode;
            try {
                decode = jwtDecoder.decode(token);
            } catch (Exception e) {
                throw new AuthenticationException(e.getMessage());
            }

            String username = decode.getSubject();
            List<String> scope = decode.getClaimAsStringList("scope");
            Long userId = decode.getClaim("userId");

            return UserBo.withUsername(username)
                    .authorities(scope.toArray(new String[0]))
                    .userId(userId)
                    .build();
        };
    }

    /**
     * 不需要鉴权 只需要验签
     */
    @Bean
    public FilterRegistrationBean<JwtBearerAuthenticationFilter> jwtBearerAuthenticationFilter(JwtTokenResolver jwtTokenResolver) {
        FilterRegistrationBean<JwtBearerAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtBearerAuthenticationFilter(new ProviderManager(new JwtAuthenticationProvider(jwtTokenResolver))));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("jwtBearerAuthenticationFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Resource
    private AuthenticationArgumentResolver authenticationArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addFirst(authenticationArgumentResolver);
    }
}

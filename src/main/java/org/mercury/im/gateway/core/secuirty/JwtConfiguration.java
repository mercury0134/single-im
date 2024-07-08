package org.mercury.im.gateway.core.secuirty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.interfaces.RSAPublicKey;

@Configuration(proxyBeanMethods = false)
public class JwtConfiguration {

    @Bean
    @ConditionalOnMissingBean
    JwtDecoder jwtDecoderByJwkKeySetUri() {
        // TODO
        String path = "keystore.jks";
        String alias = "";
        String password = "";
        ClassPathResource resource = new ClassPathResource(path);
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, password.toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyStoreKeyFactory.getKeyPair(alias, password.toCharArray()).getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}

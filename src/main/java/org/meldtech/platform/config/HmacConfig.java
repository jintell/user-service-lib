package org.meldtech.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class HmacConfig {
    @Value("${security.hmac.secret}")
    private String hmacSecret; // base64 string

    @Bean(name = "hmacSecretKey")
    public byte[] hmacSecretKey() {
        // Expect base64 to allow binary secrets
        return hmacSecret.getBytes(StandardCharsets.UTF_8);
//        return Base64.getDecoder().decode(hmacSecret);
    }
}
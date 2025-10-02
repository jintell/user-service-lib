package org.meldtech.platform.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "email")
public class VerificationLinksConfig {
    private  Verification verification;

    @Setter
    @Getter
    public static class Verification {
        // Getters and Setters
        private String link;
        private Map<String, String> links;

    }

}

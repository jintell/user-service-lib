package org.meldtech.platform.service.verifyMe;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.meldtech.platform.service.verifyMe.impl.CacVerify;
import org.meldtech.platform.service.verifyMe.impl.NinVerify;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerifyStrategyFactory {
    private final CacVerify cacVerify;
    private final NinVerify ninVerify;
    Map<String, VerifyMeStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void init() {
        strategies.put("CAC", cacVerify);
        strategies.put("NIN", ninVerify);
    }

    public VerifyMeStrategy getVerifyMeStrategy(String name) {
        return strategies.get(name);
    }
}

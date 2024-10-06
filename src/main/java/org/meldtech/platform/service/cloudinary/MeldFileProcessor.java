package org.meldtech.platform.service.cloudinary;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.meldtech.platform.exception.AppException;
import org.meldtech.platform.model.api.AppResponse;
import org.meldtech.platform.model.api.response.UploadResponse;
import org.meldtech.platform.model.dto.UploadFileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.meldtech.platform.util.AppUtil.appResponse;

@Slf4j
@Component
public class MeldFileProcessor {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    @Value("${cloudinary.api-key}")
    private String apiKey;
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        cloudinary = new Cloudinary(config);
        cloudinary.config.secure = true;
        System.out.println(cloudinary.config.cloudName);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> configureUpload() {
        // Upload the image
        return ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
    }

    @SuppressWarnings("unchecked")
    public Mono<AppResponse> uploadImage(UploadFileRequest file) {
        try {
              String resourceUrl = cloudinary.uploader()
                            .upload(file.base64Image(), configureUpload())
                            .getOrDefault("secure_url", "").toString();
            return Mono.just(appResponse(new UploadResponse(resourceUrl), "File uploaded Successfully!"));
        }catch (IOException e) {
            log.error(e.getLocalizedMessage());
            return Mono.error(new AppException("Could not upload Image"));
        }
    }
}

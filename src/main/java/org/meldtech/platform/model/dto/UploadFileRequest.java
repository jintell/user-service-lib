package org.meldtech.platform.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UploadFileRequest(@NotBlank(message = "A base 64 Image must be provided")
                                String base64Image,
                                @Pattern(regexp = "image|video|document",
                                        message = "The upload type should be image or video or document")
                                @NotBlank(message = "Resource Type not provided")
                                String resourceType) {
}

package org.meldtech.platform.model.dto;

import lombok.Builder;

@Builder
public record OAuth2RegisteredClientResponse(OAuth2RegisteredClientRecord client, AppRegistrationRecord appRegistration) {
}

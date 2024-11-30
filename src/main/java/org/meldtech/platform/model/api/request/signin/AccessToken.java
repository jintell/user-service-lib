package org.meldtech.platform.model.api.request.signin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("accessToken")
    private String accessTokenV2;
    @JsonProperty("token_type")
    private String tokenBearer;
    @JsonProperty("tokenType")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private Long timeToLive;
    @JsonProperty("expiresIn")
    private Long ttl;
    private String scope;
}

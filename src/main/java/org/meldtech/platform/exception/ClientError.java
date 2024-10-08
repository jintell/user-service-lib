package org.meldtech.platform.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
public class ClientError {
    @JsonProperty("error_description")
    private String errorDescription;
    private String error;
    @JsonProperty("error_uri")
    private String errorUri;
}

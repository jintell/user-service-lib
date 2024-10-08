package org.meldtech.platform.model.event;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@Builder
public class EmailTemplate implements Serializable {
    private String link;
    private String otp;
    private String company;
    private String firstName;
    private String username;
    private String password;
}

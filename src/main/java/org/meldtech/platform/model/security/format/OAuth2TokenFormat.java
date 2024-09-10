package org.meldtech.platform.model.security.format;

import lombok.Getter;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class OAuth2TokenFormat implements Serializable {
    @Serial
    private static final long serialVersionUID = Long.MAX_VALUE;
    public static final OAuth2TokenFormat SELF_CONTAINED;
    public static final OAuth2TokenFormat REFERENCE;
    private final String value;

    public OAuth2TokenFormat(String value) {
        Assert.hasText(value, "value cannot be empty");
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            OAuth2TokenFormat that = (OAuth2TokenFormat)obj;
            return this.getValue().equals(that.getValue());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.getValue().hashCode();
    }

    static {
//        serialVersionUID = SpringAuthorizationServerVersion.SERIAL_VERSION_UID;
        SELF_CONTAINED = new OAuth2TokenFormat("self-contained");
        REFERENCE = new OAuth2TokenFormat("reference");
    }
}

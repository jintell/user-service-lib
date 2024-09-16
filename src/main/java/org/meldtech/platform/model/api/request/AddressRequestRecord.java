package org.meldtech.platform.model.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.meldtech.platform.model.api.response.AddressRecord;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
public record AddressRequestRecord(
        @NotNull(message = "User Public Id is Required")
        String publicId,
        @NotNull(message = "Picture Name with extension is Required")
        String pictureName,
        @NotNull(message = "Document Name with extension is Required")
        String documentName,
        AddressRecord addressRecord)
{}

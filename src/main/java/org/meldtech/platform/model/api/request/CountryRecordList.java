package org.meldtech.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.meldtech.platform.model.api.response.CountryRecord;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record CountryRecordList(List<CountryRecord> countries) {
}

package org.meldtech.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.meldtech.platform.util.AppUtil;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.address")
public class Address implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String street;
    private String city;
    private String state;
    private String postCode;
    private String country;
    private String language;
    private Instant createdOn;
    @Transient
    private Integer userId;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
            id = userId;
        }
        return newRecord;
    }
}

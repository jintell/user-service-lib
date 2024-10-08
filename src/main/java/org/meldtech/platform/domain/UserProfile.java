package org.meldtech.platform.domain;


import io.r2dbc.postgresql.codec.Json;
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
 * @Date: 12/6/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.user_profile")
public class UserProfile implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String website;
    private String language;
    private String profilePicture;
    private Json settings;
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

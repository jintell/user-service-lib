package org.meldtech.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.meldtech.platform.util.AppUtil;
import org.springframework.data.annotation.Id;
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
@Table("public.user_role")
public class UserRole implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private Instant createdOn;
    private Integer userId;
    private Integer roleId;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now();
        }
        return newRecord;
    }

}

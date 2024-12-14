package org.meldtech.platform.converter;

import io.r2dbc.postgresql.codec.Json;
import org.meldtech.platform.domain.Company;
import org.meldtech.platform.model.api.response.CompanyRecord;
import org.meldtech.platform.model.constant.VerifyType;
import org.meldtech.platform.model.dto.UserSetting;
import org.meldtech.platform.model.dto.company.CacRecord;
import org.meldtech.platform.model.dto.company.NinRecord;

import java.util.Objects;

import static org.meldtech.platform.util.AppUtil.convertToType;

public class CompanyConverter {
    private CompanyConverter() {}

    public static synchronized Company mapToEntity(CompanyRecord dto) {
        return Company.builder()
                    .name(dto.name())
                    .address(dto.address())
                    .idNumber(dto.idNumber())
                    .type(dto.type())
                    .contact(dto.contact())
                    .details(Objects.isNull(dto.details()) ? null :
                            Json.of(convertToType(dto.details(), String.class)))
                    .build();
    }

    public static synchronized CompanyRecord mapToRecord(Company entity) {
       return  CompanyRecord.builder()
                    .name(entity.getName())
                    .address(entity.getAddress())
                    .idNumber(entity.getIdNumber())
                    .type(entity.getType())
                    .contact(entity.getContact())
                    .details(Objects.isNull(entity.getDetails())? null :
                            convertToType(entity.getDetails().asString(), Object.class))
                    .createdOn(entity.getCreatedOn().toString())
                    .build();
    }

    public static synchronized CompanyRecord mapToRecord(CacRecord entity) {
       return  CompanyRecord.builder()
                    .name(entity.cac().companyName())
                    .address(entity.cac().branchAddress())
                    .idNumber(entity.cac().rcNumber())
                    .type(VerifyType.CAC.name())
                    .contact(entity.cac().companyEmail())
                    .details(entity)
                    .build();
    }

    public static synchronized CompanyRecord mapToRecord(NinRecord entity) {
       return  CompanyRecord.builder()
                    .name(entity.applicant().firstName() + " " + entity.applicant().lastName())
                    .address(entity.nin().residence().address1())
                    .idNumber(entity.nin().nin())
                    .type(VerifyType.NIN.name())
                    .contact(entity.nin().phone())
                    .details(entity)
                    .build();
    }

}

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

    public static synchronized CompanyRecord mapToRecord(CacRecord entity, String address) {
       return  CompanyRecord.builder()
                    .name(entity.cac().companyName())
                    .address(getAddress(entity, address))
                    .idNumber(entity.cac().rcNumber())
                    .type(VerifyType.CAC.name())
                    .contact(entity.cac().companyEmail())
                    .details(entity)
                    .build();
    }

    private static String getAddress(CacRecord entity, String address) {
        if(Objects.isNull(entity) || Objects.isNull(entity.cac())) return address;
        return entity.cac().headOfficeAddress();
    }

    private static String getAddress(NinRecord entity, String address) {
        if(Objects.isNull(entity) || Objects.isNull(entity.nin().residence().address1())) return address;
        return entity.nin().residence().address1();
    }

    public static synchronized CompanyRecord mapToRecord(NinRecord entity, String address) {
       return  CompanyRecord.builder()
                    .name(entity.applicant().firstName() + " " + entity.applicant().lastName())
                    .address(getAddress(entity, address))
                    .idNumber(entity.nin().nin())
                    .type(VerifyType.NIN.name())
                    .contact(entity.nin().phone())
                    .details(entity)
                    .build();
    }

}

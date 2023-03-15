package ru.itmo.registrationservice.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.registrationservice.domain.enumeration.PartnerType;

@Converter(autoApply = true)
public class PartnerTypeEnumConverter implements AttributeConverter<PartnerType, String> {
    @Override
    public String convertToDatabaseColumn(PartnerType partnerType) {
        if (partnerType == null) {
            return null;
        }
        return partnerType.name();
    }

    @Override
    public PartnerType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return PartnerType.valueOf(PartnerType.class, code);
    }
}

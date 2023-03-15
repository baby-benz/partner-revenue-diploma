package ru.itmo.registrationservice.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.registrationservice.domain.enumeration.PartnerPointType;

@Converter(autoApply = true)
public class PointTypeEnumConverter implements AttributeConverter<PartnerPointType, String> {
    @Override
    public String convertToDatabaseColumn(PartnerPointType partnerType) {
        if (partnerType == null) {
            return null;
        }
        return partnerType.name();
    }

    @Override
    public PartnerPointType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return PartnerPointType.valueOf(PartnerPointType.class, code);
    }
}

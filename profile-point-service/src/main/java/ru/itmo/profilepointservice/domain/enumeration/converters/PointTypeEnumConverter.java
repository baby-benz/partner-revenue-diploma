package ru.itmo.profilepointservice.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

@Converter(autoApply = true)
public class PointTypeEnumConverter implements AttributeConverter<PointType, String> {
    @Override
    public String convertToDatabaseColumn(PointType pointType) {
        return pointType.getPointTypeCode();
    }

    @Override
    public PointType convertToEntityAttribute(String code) {
        return PointType.fromPointTypeCode(code);
    }
}

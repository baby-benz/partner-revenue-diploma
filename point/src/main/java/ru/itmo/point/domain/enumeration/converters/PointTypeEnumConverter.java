package ru.itmo.point.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.point.domain.enumeration.PointType;

@Converter(autoApply = true)
public class PointTypeEnumConverter implements AttributeConverter<PointType, String> {
    @Override
    public String convertToDatabaseColumn(PointType pointType) {
        if (pointType == null) {
            return null;
        }
        return pointType.name();
    }

    @Override
    public PointType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return PointType.valueOf(PointType.class, code);
    }
}

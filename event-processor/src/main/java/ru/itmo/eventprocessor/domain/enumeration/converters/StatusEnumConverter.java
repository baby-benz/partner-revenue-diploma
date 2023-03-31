package ru.itmo.eventprocessor.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.eventprocessor.domain.enumeration.Status;

@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public Status convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Status.valueOf(Status.class, code);
    }
}

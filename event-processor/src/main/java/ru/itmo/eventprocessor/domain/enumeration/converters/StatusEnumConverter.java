package ru.itmo.eventprocessor.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.eventprocessor.domain.enumeration.Status;

@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getStatusCode();
    }

    @Override
    public Status convertToEntityAttribute(String code) {
        return Status.fromStatusCode(code);
    }
}

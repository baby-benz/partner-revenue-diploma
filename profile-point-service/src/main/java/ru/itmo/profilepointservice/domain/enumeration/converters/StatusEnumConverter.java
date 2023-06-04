package ru.itmo.profilepointservice.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.profilepointservice.domain.enumeration.Status;

@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status status) {
        if (status == null) {
            return null;
        }
        return status.getStatusCode();
    }

    @Override
    public Status convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Status.fromStatusCode(code);
    }
}

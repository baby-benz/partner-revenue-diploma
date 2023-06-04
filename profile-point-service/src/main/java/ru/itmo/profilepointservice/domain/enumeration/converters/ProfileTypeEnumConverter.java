package ru.itmo.profilepointservice.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.profilepointservice.domain.enumeration.ProfileType;

@Converter(autoApply = true)
public class ProfileTypeEnumConverter implements AttributeConverter<ProfileType, String> {
    @Override
    public String convertToDatabaseColumn(ProfileType profileType) {
        return profileType.getProfileTypeCode();
    }

    @Override
    public ProfileType convertToEntityAttribute(String code) {
        return ProfileType.fromProfileTypeCode(code);
    }
}

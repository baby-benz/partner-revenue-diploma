package ru.itmo.profile.domain.enumeration.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.itmo.profile.domain.enumeration.ProfileType;

@Converter(autoApply = true)
public class ProfileTypeEnumConverter implements AttributeConverter<ProfileType, String> {
    @Override
    public String convertToDatabaseColumn(ProfileType profileType) {
        if (profileType == null) {
            return null;
        }
        return profileType.name();
    }

    @Override
    public ProfileType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return ProfileType.valueOf(ProfileType.class, code);
    }
}

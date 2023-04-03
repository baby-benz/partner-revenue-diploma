package ru.itmo.profile.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.profile.domain.enumeration.ProfileType;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private ProfileType profileType;

    @Column
    private Status status = Status.INACTIVE;

    public Profile(String id) {
        this.id = id;
    }

    public Profile(String id, String name, ProfileType profileType) {
        this.id = id;
        this.name = name;
        this.profileType = profileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile that = (Profile) o;
        return getId().equals(that.getId()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

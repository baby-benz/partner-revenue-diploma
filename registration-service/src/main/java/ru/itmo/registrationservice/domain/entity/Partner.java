package ru.itmo.registrationservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.registrationservice.domain.enumeration.PartnerType;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Partner {
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private PartnerType partnerType;

    public Partner(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partner that = (Partner) o;
        return getId().equals(that.getId()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

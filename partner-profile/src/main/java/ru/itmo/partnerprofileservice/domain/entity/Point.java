package ru.itmo.partnerprofileservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.partnerprofileservice.domain.enumeration.PartnerPointType;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Point {
    @Id
    private String id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "partner_profile_id")
    private Partner partnerProfile;

    @Column
    private PartnerPointType partnerPointType;

    public Point(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point that = (Point) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

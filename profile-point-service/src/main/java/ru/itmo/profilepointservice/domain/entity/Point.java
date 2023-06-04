package ru.itmo.profilepointservice.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.profilepointservice.domain.enumeration.Status;
import ru.itmo.profilepointservice.domain.enumeration.PointType;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Point {
    @Id
    @Column(columnDefinition = "uuid")
    private java.util.UUID id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @NotNull
    @Column(columnDefinition = "char(1)")
    private PointType pointType;

    @NotNull
    @Column(columnDefinition = "char(1)")
    private Status status = Status.INACTIVE;

    @Column(columnDefinition = "uuid")
    private java.util.UUID calcSchemeId;

    public Point(java.util.UUID id, String name, Profile profile, PointType pointType) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.pointType = pointType;
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

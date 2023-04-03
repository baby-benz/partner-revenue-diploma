package ru.itmo.point.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.common.domain.enumeration.Status;
import ru.itmo.point.domain.enumeration.PointType;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Point {
    @Id
    private String id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String profileId;

    @NotNull
    @Column
    private PointType pointType;

    @NotNull
    @Column
    private Status status = Status.INACTIVE;

    @Column
    private String calcSchemeId;

    public Point(String id) {
        this.id = id;
    }

    public Point(String id, String name, String profileId, PointType pointType) {
        this.id = id;
        this.name = name;
        this.profileId = profileId;
        this.pointType = pointType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point that = (Point) o;
        return id.equals(that.id) && name.equals(that.name) && profileId.equals(that.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, profileId);
    }
}

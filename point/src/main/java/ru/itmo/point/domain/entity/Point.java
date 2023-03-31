package ru.itmo.point.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column
    private String name;

    @Column
    private String profileId;

    @Column
    private PointType pointType;

    @Column
    private Status status;

    public Point(String id) {
        this.id = id;
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

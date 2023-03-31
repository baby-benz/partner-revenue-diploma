package ru.itmo.eventprocessor.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    private String id;

    @Column
    private double amount;

    @Column
    private OffsetDateTime timestamp;

    @Column
    private String profileId;

    @Column
    private String pointId;

    @Column
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Double.compare(event.getAmount(), getAmount()) == 0 &&
                getId().equals(event.getId()) &&
                getTimestamp().equals(event.getTimestamp()) &&
                getProfileId().equals(event.getProfileId()) &&
                getPointId().equals(event.getPointId()) &&
                getStatus() == event.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getTimestamp(), getProfileId(), getPointId(), getStatus());
    }
}

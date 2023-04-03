package ru.itmo.eventprocessor.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.math.BigDecimal;
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

    @NotNull
    @Column
    private BigDecimal amount;

    @NotNull
    @Column
    private OffsetDateTime timestamp;

    @NotNull
    @Column
    private String profileId;

    @NotNull
    @Column
    private String pointId;

    @Column
    private Status status = Status.NOT_PROCESSED;

    public Event(String id, BigDecimal amount, OffsetDateTime timestamp, String profileId, String pointId) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
        this.profileId = profileId;
        this.pointId = pointId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return event.getAmount().compareTo(getAmount()) == 0 &&
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

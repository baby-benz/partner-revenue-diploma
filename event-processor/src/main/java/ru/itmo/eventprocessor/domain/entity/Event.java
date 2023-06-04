package ru.itmo.eventprocessor.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import ru.itmo.eventprocessor.domain.enumeration.Status;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(scale = 6, precision = 14)
    private BigDecimal amount;

    @NotNull
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime eventTime;

    @NotNull
    @Column(columnDefinition = "UUID")
    private UUID profileId;

    @NotNull
    @Column(columnDefinition = "UUID")
    private UUID pointId;

    @Column(columnDefinition = "char(1)")
    private Status eventStatus = Status.NOT_PROCESSED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return event.getAmount().compareTo(getAmount()) == 0 &&
                getId().equals(event.getId()) &&
                getEventTime().equals(event.getEventTime()) &&
                getProfileId().equals(event.getProfileId()) &&
                getPointId().equals(event.getPointId()) &&
                getEventStatus() == event.getEventStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getEventTime(), getProfileId(), getPointId(), getEventStatus());
    }
}

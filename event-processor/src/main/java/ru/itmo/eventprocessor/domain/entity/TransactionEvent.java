package ru.itmo.eventprocessor.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@Entity
public class TransactionEvent {
    @Id
    private String id;

    @Column
    private double amount;

    @Column
    private LocalDateTime timestamp;

    @Column
    String profileId;

    @Column
    String pointId;
}

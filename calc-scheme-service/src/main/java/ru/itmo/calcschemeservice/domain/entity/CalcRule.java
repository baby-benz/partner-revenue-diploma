package ru.itmo.calcschemeservice.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CalcRule {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column
    private long amount;

    @NotNull
    @Column
    private float interestRate;

    @NotNull
    @Column
    private long bonus;
}

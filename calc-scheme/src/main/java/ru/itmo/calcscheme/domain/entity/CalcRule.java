package ru.itmo.calcscheme.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CalcRule {
    @Id
    private String id;

    @NotNull
    @Column
    private long amount;

    @NotNull
    @Column
    private float interestRate;

    @NotNull
    @Column
    private int bonus;
}

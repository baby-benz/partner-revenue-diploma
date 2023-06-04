package ru.itmo.calcschemeservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CalcScheme {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column
    private boolean isRecalc = false;

    @ManyToMany
    @JoinTable(name = "calc_scheme_rule",
            joinColumns = @JoinColumn(name="calc_scheme_id"),
            inverseJoinColumns=@JoinColumn(name="calc_rule_id"))
    private List<CalcRule> calcRules;
}

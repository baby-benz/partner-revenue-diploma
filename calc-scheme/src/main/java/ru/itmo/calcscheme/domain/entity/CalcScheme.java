package ru.itmo.calcscheme.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CalcScheme {
    @Id
    private String id;

    @Column
    private boolean isRecalc = false;

    @ManyToMany
    @JoinTable(name = "calc_scheme_rule",
            joinColumns = @JoinColumn(name="calc_scheme_id"),
            inverseJoinColumns=@JoinColumn(name="calc_rule_id"))
    private List<CalcRule> calcRules;

    public CalcScheme(String id) {
        this.id = id;
    }
}

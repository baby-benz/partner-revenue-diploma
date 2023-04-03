package ru.itmo.calcscheme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.calcscheme.domain.entity.CalcRule;

import java.util.List;
import java.util.Set;

public interface CalcRuleRepository extends JpaRepository<CalcRule, String>, JpaSpecificationExecutor<CalcRule> {
}

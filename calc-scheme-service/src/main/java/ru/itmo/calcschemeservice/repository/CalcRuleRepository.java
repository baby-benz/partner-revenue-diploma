package ru.itmo.calcschemeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.calcschemeservice.domain.entity.CalcRule;

import java.util.UUID;

public interface CalcRuleRepository extends JpaRepository<CalcRule, UUID>, JpaSpecificationExecutor<CalcRule> {
}

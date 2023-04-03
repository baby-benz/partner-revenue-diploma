package ru.itmo.calcscheme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.calcscheme.domain.entity.CalcScheme;

public interface CalcSchemeRepository extends JpaRepository<CalcScheme, String>, JpaSpecificationExecutor<CalcScheme> {
}

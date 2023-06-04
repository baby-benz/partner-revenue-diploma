package ru.itmo.calcschemeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.calcschemeservice.domain.entity.CalcScheme;

import java.util.UUID;

public interface CalcSchemeRepository extends JpaRepository<CalcScheme, UUID>, JpaSpecificationExecutor<CalcScheme> {
}

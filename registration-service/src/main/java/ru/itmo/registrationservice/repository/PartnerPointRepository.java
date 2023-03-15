package ru.itmo.registrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.registrationservice.domain.entity.Point;

public interface PartnerPointRepository extends JpaRepository<Point, String>, JpaSpecificationExecutor<Point> {
}

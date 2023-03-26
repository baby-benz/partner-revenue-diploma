package ru.itmo.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.itmo.point.domain.entity.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, String>, JpaSpecificationExecutor<Point> {
}

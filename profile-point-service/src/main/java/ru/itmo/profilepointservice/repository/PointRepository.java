package ru.itmo.profilepointservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.itmo.profilepointservice.domain.entity.Point;

import java.util.List;
import java.util.UUID;

@Repository
public interface PointRepository extends JpaRepository<Point, UUID>, JpaSpecificationExecutor<Point> {
    List<Point> findAllByProfile_Id(UUID profileId);
}

package ru.itmo.eventprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.eventprocessor.domain.entity.Event;

public interface EventRepository extends JpaRepository<Event, String>, JpaSpecificationExecutor<Event> {
}

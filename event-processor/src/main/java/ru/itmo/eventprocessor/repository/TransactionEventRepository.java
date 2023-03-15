package ru.itmo.eventprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.eventprocessor.domain.entity.TransactionEvent;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, String>, JpaSpecificationExecutor<TransactionEvent> {
}

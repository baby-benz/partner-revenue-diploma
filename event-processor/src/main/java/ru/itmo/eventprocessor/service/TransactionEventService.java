package ru.itmo.eventprocessor.service;

import ru.itmo.eventprocessor.domain.entity.TransactionEvent;

public interface TransactionEventService {
    TransactionEvent process(TransactionEvent event);
}

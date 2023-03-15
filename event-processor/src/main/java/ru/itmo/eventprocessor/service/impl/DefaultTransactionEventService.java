package ru.itmo.eventprocessor.service.impl;

import lombok.RequiredArgsConstructor;
import ru.itmo.eventprocessor.domain.entity.TransactionEvent;
import ru.itmo.eventprocessor.repository.TransactionEventRepository;
import ru.itmo.eventprocessor.service.TransactionEventService;

@RequiredArgsConstructor
public class DefaultTransactionEventService implements TransactionEventService {
    private final TransactionEventRepository eventRepository;

    @Override
    public TransactionEvent process(TransactionEvent event) {
        eventRepository.save(event);
        return null;
    }
}

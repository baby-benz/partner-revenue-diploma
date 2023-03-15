package ru.itmo.common.exception.cause;

import org.springframework.http.HttpStatus;

public interface HttpErrorCause {
    HttpStatus getStatus();
    String getMessageCode();
}

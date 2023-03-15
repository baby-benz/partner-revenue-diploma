package ru.itmo.common.web.dto.response;

public record DefaultApiErrorResponse(String errorMessageCode, String errorMessage) {
}

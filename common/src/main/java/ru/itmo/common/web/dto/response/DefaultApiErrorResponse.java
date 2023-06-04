package ru.itmo.common.web.dto.response;

import java.util.List;

public record DefaultApiErrorResponse(List<String> errorMessageCode, List<String> errorMessage) {
}

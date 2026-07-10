package com.aplication.bankapi.exception.errors;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> errors) {
}


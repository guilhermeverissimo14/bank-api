package com.aplication.bankapi.dto.cliente;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        LocalDateTime createdAt
) {
}

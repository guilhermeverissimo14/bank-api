package com.aplication.bankapi.dto.conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aplication.bankapi.enums.StatusConta;

public record ContaResponse(
        Long id,
        String numero,
        String agencia,
        BigDecimal saldo,
        StatusConta status,
        Long clienteId,
        LocalDateTime createdAt
) {
    
}

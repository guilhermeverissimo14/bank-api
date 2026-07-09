package com.aplication.bankapi.dto.conta;

import java.math.BigDecimal;

public record SaldoResponse(
        Long contaId,
        BigDecimal saldo
) {
    
}

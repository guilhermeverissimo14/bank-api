package com.aplication.bankapi.dto.conta;

public record SaldoResponse(
        Long contaId,
        double saldo
) {
    
}

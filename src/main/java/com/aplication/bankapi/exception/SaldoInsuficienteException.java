package com.aplication.bankapi.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Long contaId, BigDecimal saldoDisponivel) {
       super("Saldo insuficiente na conta " + contaId + ". Saldo disponível: " + saldoDisponivel);
    }
    
}

package com.aplication.bankapi.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Long contaId, double saldoDisponivel) {
       super("Saldo insuficiente na conta " + contaId + ". Saldo disponível: " + saldoDisponivel);
    }
    
}

package com.aplication.bankapi.exception;

public class SaldoNaoZeradoException extends RuntimeException {
    public SaldoNaoZeradoException(long contaId) {
        super("Não é possível excluir a conta " + contaId + " porque o saldo não está zerado");
    }
    
}

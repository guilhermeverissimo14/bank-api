package com.aplication.bankapi.exception;

public class TransferenciaParaMesmaContaException extends RuntimeException {
    public TransferenciaParaMesmaContaException(Long contaId) {
        super("Não é possível transferir para a mesma conta. Conta ID: " + contaId);
    }
    
}

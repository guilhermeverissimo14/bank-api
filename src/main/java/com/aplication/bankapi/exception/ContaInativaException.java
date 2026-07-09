package com.aplication.bankapi.exception;

public class ContaInativaException extends RuntimeException {
    public ContaInativaException(Long contaId) {
        super("A conta " + contaId + " não está ativa e não pode receber operações");
    }
}

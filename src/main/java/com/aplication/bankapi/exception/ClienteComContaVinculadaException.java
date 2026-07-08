package com.aplication.bankapi.exception;

public class ClienteComContaVinculadaException extends RuntimeException {
    public ClienteComContaVinculadaException(long clienteId) {
         super("Não é possível excluir o cliente " + clienteId + " porque existem contas vinculadas a ele");
    }
}

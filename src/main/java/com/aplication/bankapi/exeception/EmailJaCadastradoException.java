package com.aplication.bankapi.exeception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Já existe um cliente cadastrado com o email: " + email);
    }
}


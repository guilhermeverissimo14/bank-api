package com.aplication.bankapi.dto.auth;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos
) {

    public LoginResponse(String token, long expiraEmSegundos) {
        this(token, "Bearer", expiraEmSegundos);
    }
}


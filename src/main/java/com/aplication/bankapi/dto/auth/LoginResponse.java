package com.aplication.bankapi.dto.auth;

public record LoginResponse(
        long id,
        String nome,
        String email,
        String token,
        long expiraEmSegundos
) {
}


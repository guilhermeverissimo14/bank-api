package com.aplication.bankapi.dto.conta;

import jakarta.validation.constraints.NotNull;

public record AbrirContaRequest(
        @NotNull(message = "Cliente é obrigatório")
        Long clienteId
) {
}


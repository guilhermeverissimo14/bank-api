package com.aplication.bankapi.dto.conta;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record TransacaoRequest(
        @NotNull(message = "Número da conta é obrigatório")
        String numeroConta,

        @NotNull(message = "Agência é obrigatória")
        String agencia,
        
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "Valor deve ter no máximo 2 casas decimais")
        BigDecimal valor) {
}

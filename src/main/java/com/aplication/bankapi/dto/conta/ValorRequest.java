package com.aplication.bankapi.dto.conta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ValorRequest(
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "Valor deve ter no máximo 2 casas decimais")
        BigDecimal valor
) {
}

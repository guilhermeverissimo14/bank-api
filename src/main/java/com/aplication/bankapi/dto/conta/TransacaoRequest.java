package com.aplication.bankapi.dto.conta;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransacaoRequest(
        @NotNull(message = "Número da conta é obrigatório")
        String numeroConta,

        @NotNull(message = "Agência é obrigatória")
        String agencia,
        
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        BigDecimal valor) {

}

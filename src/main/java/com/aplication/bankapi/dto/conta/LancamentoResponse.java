package com.aplication.bankapi.dto.conta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aplication.bankapi.enums.TipoLancamento;

public record LancamentoResponse(
        Long id,
        TipoLancamento tipo,
        BigDecimal valor,
        BigDecimal saldoApos,
        LocalDateTime dataHora) {

}

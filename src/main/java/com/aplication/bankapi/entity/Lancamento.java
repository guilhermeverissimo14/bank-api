package com.aplication.bankapi.entity;

import com.aplication.bankapi.enums.TipoLancamento;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lancamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 30)
    private TipoLancamento tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "saldo_apos", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoApos;

    @CreationTimestamp
    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;
}

package com.aplication.bankapi.entity;

import com.aplication.bankapi.enums.StatusConta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta", uniqueConstraints = {
        @UniqueConstraint(name = "uk_conta_numero", columnNames = "numero")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) 
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "agencia", nullable = false, length = 10)
    private String agencia;

    @Column(name = "numero", nullable = false, length = 20)
    private String numero;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusConta status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

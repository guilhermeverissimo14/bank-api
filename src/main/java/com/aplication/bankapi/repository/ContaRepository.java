package com.aplication.bankapi.repository;

import com.aplication.bankapi.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    boolean existsByNumero(String numero);

    boolean existsByClienteId(long clienteId);
    
    Optional<Conta> findByNumeroAndAgencia(String numero, String agencia);

    List<Conta> findByClienteId(Long clienteId);
}


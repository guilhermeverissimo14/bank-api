package com.aplication.bankapi.repository;

import com.aplication.bankapi.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    boolean existsByNumero(String numero);

    boolean existsByClienteId(long clienteId);
    
    List<Conta> findByClienteId(Long clienteId);
}


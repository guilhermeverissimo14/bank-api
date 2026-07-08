package com.aplication.bankapi.repository;

import com.aplication.bankapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

}

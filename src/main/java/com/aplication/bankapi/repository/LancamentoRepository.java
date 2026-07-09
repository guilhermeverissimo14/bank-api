package com.aplication.bankapi.repository;

import com.aplication.bankapi.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findByContaIdOrderByDataHoraDesc(Long contaId);
}


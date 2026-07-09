package com.aplication.bankapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.entity.Conta;
import com.aplication.bankapi.enums.StatusConta;
import com.aplication.bankapi.exception.ResourceNotFoundException;
import com.aplication.bankapi.exception.SaldoNaoZeradoException;
import com.aplication.bankapi.repository.ClienteRepository;
import com.aplication.bankapi.repository.ContaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {
    private static final String AGENCIA_PADRAO = "0001";

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;

    public ContaResponse abrirConta(AbrirContaRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cliente não encontrado com id: " + request.clienteId()));

        Conta conta = Conta.builder()
                .numero(gerarNumeroConta())
                .agencia(AGENCIA_PADRAO)
                .saldo(BigDecimal.ZERO)
                .status(StatusConta.ATIVA)
                .cliente(cliente)
                .build();

        return toResponse(contaRepository.save(conta));
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listar() {
        return contaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true) 
    public ContaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    public ContaResponse encerrar(Long id) {
        Conta conta = buscarEntidadePorId(id);

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new SaldoNaoZeradoException(id);
        }

        conta.setStatus(StatusConta.ENCERRADA);

        return toResponse(conta);
    }

    private Conta buscarEntidadePorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com id: " + id));
    }

    private String gerarNumeroConta() {
        String numero;
        do {
            numero = String.valueOf((int) (Math.random() * 1000000));
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }

    private ContaResponse toResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getStatus(),
                conta.getCliente().getId(),
                conta.getCreatedAt());
    }
}

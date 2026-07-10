package com.aplication.bankapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.dto.conta.LancamentoResponse;
import com.aplication.bankapi.dto.conta.SaldoResponse;
import com.aplication.bankapi.dto.conta.ValorRequest;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.entity.Conta;
import com.aplication.bankapi.entity.Lancamento;
import com.aplication.bankapi.enums.StatusConta;
import com.aplication.bankapi.enums.TipoLancamento;
import com.aplication.bankapi.exception.ContaInativaException;
import com.aplication.bankapi.exception.ResourceNotFoundException;
import com.aplication.bankapi.exception.SaldoInsuficienteException;
import com.aplication.bankapi.exception.SaldoNaoZeradoException;
import com.aplication.bankapi.repository.ClienteRepository;
import com.aplication.bankapi.repository.ContaRepository;
import com.aplication.bankapi.repository.LancamentoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {
    private static final String AGENCIA_PADRAO = "0001";

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

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

        Conta salvaConta = contaRepository.save(conta);
        log.info("Conta aberta: id={}, numero={}, clienteId={}", salvaConta.getId(), salvaConta.getNumero(),
                cliente.getId());

        return toResponse(salvaConta);
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

    public ContaResponse depositar(Long id, ValorRequest request) {
        Conta conta = buscarEntidadePorId(id);
        validarContaAtiva(conta);

        conta.setSaldo(conta.getSaldo().add(request.valor()));
        registrarLancamento(conta, TipoLancamento.DEPOSITO, request.valor());
        log.info("Depósito de {} na conta {}. Novo saldo: {}", request.valor(), id, conta.getSaldo());
        return toResponse(conta);
    }

    public ContaResponse sacar(Long id, ValorRequest request) {
        Conta conta = buscarEntidadePorId(id);
        validarContaAtiva(conta);

        if (conta.getSaldo().compareTo(request.valor()) < 0) {
            log.warn("Saque recusado por saldo insuficiente: conta={}, valorSolicitado={}, saldoDisponivel={}",
                    id, request.valor(), conta.getSaldo());
            throw new SaldoInsuficienteException(id, conta.getSaldo());
        }

        conta.setSaldo(conta.getSaldo().subtract(request.valor()));
        registrarLancamento(conta, TipoLancamento.SAQUE, request.valor());
        log.info("Saque de {} na conta {}. Novo saldo: {}", request.valor(), id, conta.getSaldo());
        return toResponse(conta);
    }

    @Transactional(readOnly = true)
    public SaldoResponse consultarSaldo(Long id) {
        Conta conta = buscarEntidadePorId(id);
        return new SaldoResponse(conta.getId(), conta.getSaldo());
    }

    @Transactional(readOnly = true)
    public List<LancamentoResponse> extrato(Long id) {
        buscarEntidadePorId(id);

        return lancamentoRepository.findByContaIdOrderByDataHoraDesc(id).stream()
                .map(l -> new LancamentoResponse(l.getId(), l.getTipo(), l.getValor(), l.getSaldoApos(),
                        l.getDataHora()))
                .toList();
    }

    public ContaResponse encerrar(Long id) {
        Conta conta = buscarEntidadePorId(id);

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Encerramento recusado por saldo não zerado: conta={}, saldo={}", id, conta.getSaldo());
            throw new SaldoNaoZeradoException(id);
        }

        conta.setStatus(StatusConta.ENCERRADA);
        log.info("Conta encerrada: id={}", id);
        return toResponse(conta);
    }

    private void validarContaAtiva(Conta conta) {
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new ContaInativaException(conta.getId());
        }
    }

    private void registrarLancamento(Conta conta, TipoLancamento tipo, BigDecimal valor) {
        Lancamento lancamento = Lancamento.builder()
                .conta(conta)
                .tipo(tipo)
                .valor(valor)
                .saldoApos(conta.getSaldo())
                .build();

        lancamentoRepository.save(lancamento);
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

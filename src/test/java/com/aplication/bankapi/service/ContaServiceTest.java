package com.aplication.bankapi.service;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.dto.conta.TransacaoRequest;
import com.aplication.bankapi.dto.conta.ValorRequest;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.entity.Conta;
import com.aplication.bankapi.entity.Lancamento;
import com.aplication.bankapi.enums.StatusConta;
import com.aplication.bankapi.exception.ContaInativaException;
import com.aplication.bankapi.exception.ResourceNotFoundException;
import com.aplication.bankapi.exception.SaldoInsuficienteException;
import com.aplication.bankapi.exception.SaldoNaoZeradoException;
import com.aplication.bankapi.exception.TransferenciaParaMesmaContaException;
import com.aplication.bankapi.repository.ClienteRepository;
import com.aplication.bankapi.repository.ContaRepository;
import com.aplication.bankapi.repository.LancamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private LancamentoRepository lancamentoRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    void deveAbrirContaComSucessoQuandoClienteExiste() {
        Cliente cliente = Cliente.builder().id(1L).nome("Maria").email("maria@teste.com").build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta salva = invocation.getArgument(0);
            salva.setId(10L);
            return salva;
        });

        ContaResponse response = contaService.abrirConta(new AbrirContaRequest(1L));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.saldo()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.status()).isEqualTo(StatusConta.ATIVA);
        assertThat(response.clienteId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoAoAbrirContaComClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contaService.abrirConta(new AbrirContaRequest(99L)));
    }

    @Test
    void deveDepositarComSucessoQuandoContaAtiva() {
        Conta conta = contaAtiva(1L, new BigDecimal("100.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponse response = contaService.depositar(1L, new ValorRequest(new BigDecimal("50.00")));

        assertThat(response.saldo()).isEqualByComparingTo(new BigDecimal("160.00"));
        verify(lancamentoRepository).save(any(Lancamento.class));
    }

    @Test
    void deveLancarExcecaoAoDepositarEmContaInativa() {
        Conta conta = contaComStatus(1L, StatusConta.BLOQUEADA, BigDecimal.ZERO);
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThrows(ContaInativaException.class,
                () -> contaService.depositar(1L, new ValorRequest(new BigDecimal("50.00"))));
    }

    @Test
    void deveSacarComSucessoQuandoSaldoSuficiente() {
        Conta conta = contaAtiva(1L, new BigDecimal("100.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponse response = contaService.sacar(1L, new ValorRequest(new BigDecimal("40.00")));

        assertThat(response.saldo()).isEqualByComparingTo(new BigDecimal("60.00"));
    }

    @Test
    void deveLancarExcecaoAoSacarComSaldoInsuficiente() {
        Conta conta = contaAtiva(1L, new BigDecimal("10.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThrows(SaldoInsuficienteException.class,
                () -> contaService.sacar(1L, new ValorRequest(new BigDecimal("50.00"))));
    }

    @Test
    void deveEncerrarContaComSaldoZerado() {
        Conta conta = contaAtiva(1L, BigDecimal.ZERO);
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponse response = contaService.encerrar(1L);

        assertThat(response.status()).isEqualTo(StatusConta.ENCERRADA);
    }

    @Test
    void deveLancarExcecaoAoEncerrarContaComSaldoDiferenteDeZero() {
        Conta conta = contaAtiva(1L, new BigDecimal("10.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThrows(SaldoNaoZeradoException.class, () -> contaService.encerrar(1L));
    }

    @Test
    void deveTransferirComSucessoEntreContasAtivas() {
        Conta origem = contaAtiva(1L, new BigDecimal("100.00"));
        Conta destino = contaAtiva(2L, new BigDecimal("20.00"));
        destino.setNumero("999999");

        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroAndAgencia("999999", "0001")).thenReturn(Optional.of(destino));

        TransacaoRequest request = new TransacaoRequest("999999", "0001", new BigDecimal("30.00"));

        ContaResponse response = contaService.transferir(1L, request);

        assertThat(response.saldo()).isEqualByComparingTo(new BigDecimal("70.00"));
        assertThat(destino.getSaldo()).isEqualByComparingTo(new BigDecimal("50.00"));
        verify(lancamentoRepository, times(2)).save(any(Lancamento.class));
    }

    @Test
    void deveLancarExcecaoAoTransferirParaMesmaConta() {
        Conta origem = contaAtiva(1L, new BigDecimal("100.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroAndAgencia("123456", "0001")).thenReturn(Optional.of(origem));

        TransacaoRequest request = new TransacaoRequest("123456", "0001", new BigDecimal("10.00"));

        assertThrows(TransferenciaParaMesmaContaException.class, () -> contaService.transferir(1L, request));
    }

    @Test
    void deveLancarExcecaoAoTransferirComContaDestinoNaoEncontrada() {
        Conta origem = contaAtiva(1L, new BigDecimal("100.00"));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(origem));
        when(contaRepository.findByNumeroAndAgencia("000000", "0001")).thenReturn(Optional.empty());

        TransacaoRequest request = new TransacaoRequest("000000", "0001", new BigDecimal("10.00"));

        assertThrows(ResourceNotFoundException.class, () -> contaService.transferir(1L, request));
    }

    private Conta contaAtiva(Long id, BigDecimal saldo) {
        return contaComStatus(id, StatusConta.ATIVA, saldo);
    }

    private Conta contaComStatus(Long id, StatusConta status, BigDecimal saldo) {
        Cliente cliente = Cliente.builder().id(1L).nome("Cliente Teste").email("teste@teste.com").build();
        return Conta.builder()
                .id(id)
                .numero("123456")
                .agencia("0001")
                .saldo(saldo)
                .status(status)
                .cliente(cliente)
                .createdAt(LocalDateTime.now())
                .build();
    }
}


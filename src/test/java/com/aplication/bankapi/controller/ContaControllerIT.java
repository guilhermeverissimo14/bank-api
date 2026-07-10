package com.aplication.bankapi.controller;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ValorRequest;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.entity.Conta;
import com.aplication.bankapi.enums.StatusConta;
import com.aplication.bankapi.repository.ClienteRepository;
import com.aplication.bankapi.repository.ContaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.aplication.bankapi.TestcontainersConfiguration;


import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class ContaControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveAbrirContaComSucesso() throws Exception {
        Cliente cliente = clienteRepository.save(Cliente.builder()
                .nome("Maria")
                .email("maria@teste.com")
                .senha(passwordEncoder.encode("senha123"))
                .build());

        mockMvc.perform(post("/contas")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AbrirContaRequest(cliente.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(0))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/contas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar404AoBuscarContaInexistente() throws Exception {
        mockMvc.perform(get("/contas/999999").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deveDepositarEConsultarSaldo() throws Exception {
        Cliente cliente = clienteRepository.save(Cliente.builder()
                .nome("João")
                .email("joao@teste.com")
                .senha(passwordEncoder.encode("senha123"))
                .build());

        Conta conta = contaRepository.save(Conta.builder()
                .numero("555555")
                .agencia("0001")
                .saldo(BigDecimal.ZERO)
                .status(StatusConta.ATIVA)
                .cliente(cliente)
                .build());

        mockMvc.perform(post("/contas/{id}/depositar", conta.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ValorRequest(new BigDecimal("100.00")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(100.00));

        mockMvc.perform(get("/contas/{id}/saldo", conta.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(100.00));
    }
}

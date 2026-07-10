package com.aplication.bankapi.controller;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.dto.conta.SaldoResponse;
import com.aplication.bankapi.dto.conta.TransacaoRequest;
import com.aplication.bankapi.dto.conta.ValorRequest;
import com.aplication.bankapi.exception.errors.ErrorResponse;
import com.aplication.bankapi.exception.errors.ValidationErrorResponse;
import com.aplication.bankapi.service.ContaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Contas", description = "Abertura e movimentação de contas bancárias")
@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContaController {

    private final ContaService contaService;

    @Operation(summary = "Abrir conta", description = "Abre uma conta nova para um cliente já cadastrado, com saldo inicial zero e status ATIVA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta aberta com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (email ou senha ausentes/mal formatados)", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@Valid @RequestBody AbrirContaRequest request) {
        ContaResponse response = contaService.abrirConta(request);
        return ResponseEntity.created(URI.create("/contas/" + response.id())).body(response);
    }

    @Operation(summary = "Listar contas", description = "Lista todas as contas cadastradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar());
    }

    @Operation(summary = "Buscar conta por id", description = "Retorna os dados de uma conta específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @Operation(summary = "Encerrar conta", description = "Encerra a conta, mudando o status para ENCERRADA. Só é permitido com o saldo zerado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conta com saldo diferente de zero", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<ContaResponse> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.encerrar(id));
    }

    @Operation(summary = "Depositar", description = "Deposita um valor na conta, atualiza o saldo e registra o lançamento no extrato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido (ausente, zero ou negativo)", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conta não está ativa", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/depositar")
    public ResponseEntity<ContaResponse> depositar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.depositar(id, request));
    }

    @Operation(summary = "Sacar", description = "Saca um valor da conta, validando saldo disponível, atualiza o saldo e registra o lançamento no extrato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saque realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido (ausente, zero ou negativo)", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente ou conta não está ativa", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/sacar")
    public ResponseEntity<ContaResponse> sacar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.sacar(id, request));
    }

    @Operation(summary = "Transferir", description = "Transfere um valor da conta para outra, validando saldo disponível, atualiza os saldos e registra os lançamentos no extrato.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Valor inválido (ausente, zero ou negativo) ou dados da conta destino inválidos", content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta origem ou destino não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente, conta não está ativa ou tentativa de transferência para a mesma conta", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/transferir")
    public ResponseEntity<ContaResponse> transferir(@PathVariable Long id, @Valid @RequestBody TransacaoRequest request) {
        return ResponseEntity.ok(contaService.transferir(id, request));
    }

    @Operation(summary = "Consultar saldo", description = "Retorna o saldo atual da conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoResponse> consultarSaldo(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }
}
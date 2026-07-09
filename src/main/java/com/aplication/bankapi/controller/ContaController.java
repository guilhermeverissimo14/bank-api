package com.aplication.bankapi.controller;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.dto.conta.LancamentoResponse;
import com.aplication.bankapi.dto.conta.SaldoResponse;
import com.aplication.bankapi.dto.conta.ValorRequest;
import com.aplication.bankapi.service.ContaService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaResponse> abrir(@Valid @RequestBody AbrirContaRequest request) {
        ContaResponse response = contaService.abrirConta(request);
        return ResponseEntity.created(URI.create("/contas/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<ContaResponse> encerrar(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.encerrar(id));
    }

    // Endpoints para depositar, sacar, consultar saldo e extrato
    @PostMapping("/{id}/depositar")
    public ResponseEntity<ContaResponse> depositar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.depositar(id, request));
    }

    @PostMapping("/{id}/sacar")
    public ResponseEntity<ContaResponse> sacar(@PathVariable Long id, @Valid @RequestBody ValorRequest request) {
        return ResponseEntity.ok(contaService.sacar(id, request));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<SaldoResponse> consultarSaldo(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<LancamentoResponse>> extrato(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.extrato(id));
    }

}

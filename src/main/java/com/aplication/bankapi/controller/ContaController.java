package com.aplication.bankapi.controller;

import com.aplication.bankapi.dto.conta.AbrirContaRequest;
import com.aplication.bankapi.dto.conta.ContaResponse;
import com.aplication.bankapi.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
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
}


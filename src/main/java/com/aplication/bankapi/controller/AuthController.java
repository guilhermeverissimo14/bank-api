package com.aplication.bankapi.controller;

import com.aplication.bankapi.dto.auth.LoginRequest;
import com.aplication.bankapi.dto.auth.LoginResponse;
import com.aplication.bankapi.exception.ErrorResponse;
import com.aplication.bankapi.exception.ValidationErrorResponse;
import com.aplication.bankapi.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Autentica um cliente com email e senha, e retorna um token JWT (válido por 2 horas) a ser usado no header Authorization das demais rotas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, token retornado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (email ou senha ausentes/mal formatados)",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Email ou senha incorretos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}


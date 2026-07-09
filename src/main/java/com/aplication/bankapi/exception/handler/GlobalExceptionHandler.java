package com.aplication.bankapi.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aplication.bankapi.exception.ClienteComContaVinculadaException;
import com.aplication.bankapi.exception.ContaInativaException;
import com.aplication.bankapi.exception.CredenciaisInvalidasException;
import com.aplication.bankapi.exception.EmailJaCadastradoException;
import com.aplication.bankapi.exception.ResourceNotFoundException;
import com.aplication.bankapi.exception.SaldoInsuficienteException;
import com.aplication.bankapi.exception.SaldoNaoZeradoException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Erro de validação");
        body.put("erros", erros);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(ClienteComContaVinculadaException.class)
    public ResponseEntity<Map<String, Object>> handleClienteComContaVinculada(ClienteComContaVinculadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(SaldoNaoZeradoException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoNaoZerado(SaldoNaoZeradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ContaInativaException.class)
    public ResponseEntity<Map<String, Object>> handleContaInativa(ContaInativaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    private Map<String, Object> buildBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("erro", status.getReasonPhrase());
        body.put("mensagem", message);
        return body;
    }
}

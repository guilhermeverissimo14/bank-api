package com.aplication.bankapi.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
import com.aplication.bankapi.exception.TransferenciaParaMesmaContaException;
import com.aplication.bankapi.exception.errors.ErrorResponse;
import com.aplication.bankapi.exception.errors.ValidationErrorResponse;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j // esse é do lombok, ele cria logs automatico
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponse> handleConflict(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // Quando bate no @Valid e não passa na validação, cai aqui
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        ValidationErrorResponse body = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação",
                erros);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(ClienteComContaVinculadaException.class)
    public ResponseEntity<ErrorResponse> handleClienteComContaVinculada(ClienteComContaVinculadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(SaldoNaoZeradoException.class)
    public ResponseEntity<ErrorResponse> handleSaldoNaoZerado(SaldoNaoZeradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ContaInativaException.class)
    public ResponseEntity<ErrorResponse> handleContaInativa(ContaInativaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // nullPointerExeception erro inesperado, cai aqui
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor"));
    }

    // Quando o corpo da requisição é inválido ou malformado, cai aqui
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(buildBody(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado"));
    }

    @ExceptionHandler(TransferenciaParaMesmaContaException.class)
    public ResponseEntity<ErrorResponse> handleTransferenciaParaMesmaConta(TransferenciaParaMesmaContaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    private ErrorResponse buildBody(HttpStatus status, String message) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
    }
}

package com.aplication.bankapi.service.auth;

import com.aplication.bankapi.dto.auth.LoginRequest;
import com.aplication.bankapi.dto.auth.LoginResponse;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.exception.CredenciaisInvalidasException;
import com.aplication.bankapi.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Cliente cliente = clienteRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não cadastrado: {}", request.email());
                    return new CredenciaisInvalidasException();
                });

        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) { // matches compara a senha fornecida com a criptografada.
            log.warn("Tentativa de login com credenciais inválidas: email={}", request.email());
            throw new CredenciaisInvalidasException();
        }

        String token = jwtService.gerarToken(cliente.getId(), cliente.getEmail());

        return new LoginResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                token,
                jwtService.getExpirationSeconds());
    }
}

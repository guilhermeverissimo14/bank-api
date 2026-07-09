package com.aplication.bankapi.service.auth;

import com.aplication.bankapi.dto.auth.LoginRequest;
import com.aplication.bankapi.dto.auth.LoginResponse;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.exception.CredenciaisInvalidasException;
import com.aplication.bankapi.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Cliente cliente = clienteRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) { // matches compara a senha fornecida com a
                                                                             // criptografada.
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

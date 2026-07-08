package com.aplication.bankapi.service.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secretKey;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String gerarToken(Long clienteId, String email) {
        try {
            Instant agora = Instant.now();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(email)
                    .claim("clienteId", clienteId)
                    .issueTime(Date.from(agora))
                    .expirationTime(Date.from(agora.plusSeconds(expirationMinutes * 60)))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(new MACSigner(secretKey));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Erro ao gerar token JWT", e);
        }
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

}

package com.aplication.bankapi.service;

import com.aplication.bankapi.dto.cliente.ClienteRequest;
import com.aplication.bankapi.dto.cliente.ClienteResponse;
import com.aplication.bankapi.dto.cliente.ClienteUpdateRequest;
import com.aplication.bankapi.entity.Cliente;
import com.aplication.bankapi.exception.ClienteComContaVinculadaException;
import com.aplication.bankapi.exception.EmailJaCadastradoException;
import com.aplication.bankapi.exception.ResourceNotFoundException;
import com.aplication.bankapi.repository.ClienteRepository;
import com.aplication.bankapi.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;


    public ClienteResponse criar(ClienteRequest request) {
        if (clienteRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .build();

        Cliente salvaCliente = clienteRepository.save(cliente);
        log.info("Cliente criado com sucesso: {}", salvaCliente.getId());
        return toResponse(salvaCliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String nome) {

        List<Cliente> clientes = (nome == null || nome.isEmpty()) ? clienteRepository.findAll() : clienteRepository.findByNomeContainingIgnoreCase(nome);
        
        return clientes.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadePorId(id));
    }

    //dirty checking do JPA, não precisa chamar o save, ele atualiza automaticamente
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest request) {
        Cliente cliente = buscarEntidadePorId(id);

        if (!cliente.getEmail().equals(request.email()) && clienteRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        log.info("Cliente atualizado com sucesso: {}", cliente.getId());  
        return toResponse(cliente);
    }

    public void deletar(Long id) {

        if(contaRepository.existsByClienteId(id)) {
            throw new ClienteComContaVinculadaException(id);
        }

        clienteRepository.delete(buscarEntidadePorId(id));
        log.info("Cliente deletado com sucesso: {}", id);
    }

    private Cliente buscarEntidadePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com id: " + id));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getCreatedAt());
    }
}


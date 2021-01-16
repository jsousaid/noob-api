package com.joao.noob.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joao.noob.domain.exception.ServiceException;
import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.repository.ClientRepository;

@Service
public class ClientService {

    private static final String EMAIL_ALREADY_EXISTS_MESSAGE = "Já existe um cliente cadastrado com este e-mail";
    private static final String CLIENT_NOT_FOUND_MESSAGE = "Não foi encontrado um cliente com este idenficador";

    @Autowired
    private ClientRepository repository;

    public List<Client> findAll() {
        return repository.findAll();
    }

    public Optional<Client> findById(UUID id) {
        return repository.findById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        if (findByEmail(email) != null)
            return true;
        else
            return false;
    }

    public Client findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Client create(Client client) {

        if (existsByEmail(client.getEmail()))
            throw new ServiceException(EMAIL_ALREADY_EXISTS_MESSAGE);

        return repository.save(client);
    }

    public Client update(UUID id, Client client) {

        var clientRegistered = findById(id);

        if (!existsById(id))
            throw new ServiceException(CLIENT_NOT_FOUND_MESSAGE);

        if (existsByEmail(client.getEmail())
                && !clientRegistered.get().getEmail().equals(client.getEmail()))
            throw new ServiceException(EMAIL_ALREADY_EXISTS_MESSAGE);

        client.setId(id);
        Client clientUpdated = repository.save(client);

        return clientUpdated;
    }

    public void deleteById(UUID id) {
        if (existsById(id))
            repository.deleteById(id);
    }

}

package com.joao.noob.api.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.repository.ClientRepository;

public class Utils {

    @Autowired
    private ClientRepository repository;

    public Client createClient(String name, String email, String phoneNumber) {

        Client client = new Client();

        client.setName(name);
        client.setEmail(email);
        client.setPhoneNumber(phoneNumber);

        return repository.save(client);

    }

}

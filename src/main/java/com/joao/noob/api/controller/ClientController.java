package com.joao.noob.api.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.service.ClientService;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService service;

    @GetMapping
    public ResponseEntity<List<Client>> getAll() {

        List<Client> clients = service.findAll();

        if (clients.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable UUID id) {

        Optional<Client> client = service.findById(id);

        if (client.isPresent())
            return ResponseEntity.ok(client.get());
        else
            return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody Client client) {

        Client newClient = service.create(client);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newClient.getId().toString())
                .toUri();

        return ResponseEntity.created(location).body(newClient);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable UUID id,
            @Valid @RequestBody Client client) {

        if (service.existsById(id)) {

            service.update(id, client);

            return ResponseEntity.ok(client);

        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        if (service.existsById(id)) {

            service.deleteById(id);

            return ResponseEntity.noContent().build();

        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

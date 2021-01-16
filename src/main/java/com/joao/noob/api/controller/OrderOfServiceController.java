package com.joao.noob.api.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.joao.noob.domain.model.OrderOfService;
import com.joao.noob.domain.service.OrderOfServiceService;

@RestController
@RequestMapping("/orders-of-service")
public class OrderOfServiceController {

    @Autowired
    private OrderOfServiceService service;

    @PostMapping
    public ResponseEntity<OrderOfService> create(
            @Valid @RequestBody OrderOfService orderOfService) {

        OrderOfService created = service.create(orderOfService);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}/").buildAndExpand(created.getId().toString())
                .toUri();

        return ResponseEntity.created(location).body(created);

    }

    @GetMapping
    public ResponseEntity<List<OrderOfService>> getAll() {
        List<OrderOfService> orders = service.findAll();

        if (orders.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(orders);

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderOfService> getById(@PathVariable UUID id) {

        Optional<OrderOfService> order = service.findById(id);

        if (order.isPresent())
            return ResponseEntity.ok(order.get());
        else
            return ResponseEntity.notFound().build();
    }

}

package com.joao.noob.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joao.noob.domain.exception.ServiceException;
import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.model.OrderOfService;
import com.joao.noob.domain.model.OrderOfServiceStatuses;
import com.joao.noob.domain.repository.ClientRepository;
import com.joao.noob.domain.repository.OrderOfServiceRepository;

@Service
public class OrderOfServiceService {

    private static final String CLIENT_NOT_FOUND_MESSAGE = "O Cliente informado não foi encontrado!";

    @Autowired
    private OrderOfServiceRepository orderOfServiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Optional<OrderOfService> findById(UUID id) {
        return orderOfServiceRepository.findById(id);
    }

    public OrderOfService create(OrderOfService orderOfService) {

        Client client = clientRepository
                .findById(orderOfService.getClient().getId()).orElseThrow(
                        () -> new ServiceException(CLIENT_NOT_FOUND_MESSAGE));

        orderOfService.setStatus(OrderOfServiceStatuses.OPENED);
        orderOfService.setOpenedDate(OffsetDateTime.now());
        orderOfService.setClient(client);

        return orderOfServiceRepository.save(orderOfService);

    }

    public List<OrderOfService> findAll() {
        return orderOfServiceRepository.findAll();
    }

}

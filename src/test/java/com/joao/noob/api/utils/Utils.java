package com.joao.noob.api.utils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.model.OrderOfService;
import com.joao.noob.domain.model.OrderOfServiceStatuses;
import com.joao.noob.domain.repository.ClientRepository;
import com.joao.noob.domain.repository.OrderOfServiceRepository;

public class Utils {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private OrderOfServiceRepository orderOfServiceRepository;

    public Client createClient(String name, String email, String phoneNumber) {

        Client client = new Client();

        client.setName(name);
        client.setEmail(email);
        client.setPhoneNumber(phoneNumber);

        return repository.save(client);

    }

    public OrderOfService createOrderOfService(Client client,
            String description, String price) {

        OrderOfService orderOfService = new OrderOfService();

        orderOfService.setDescription(description);
        orderOfService.setPrice(new BigDecimal(price));
        orderOfService.setStatus(OrderOfServiceStatuses.OPENED);
        orderOfService.setOpenedDate(OffsetDateTime.now());
        orderOfService.setClient(client);

        return orderOfServiceRepository.save(orderOfService);

    }

}

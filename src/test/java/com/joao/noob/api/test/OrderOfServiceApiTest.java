package com.joao.noob.api.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.joao.noob.api.utils.Utils;
import com.joao.noob.domain.model.Client;
import com.joao.noob.domain.model.OrderOfService;
import com.joao.noob.domain.model.OrderOfServiceStatuses;
import com.joao.noob.domain.repository.OrderOfServiceRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderOfServiceApiTest extends Utils {

    private static final String PATH = "/orders-of-service";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderOfServiceRepository orderRepository;

    @BeforeEach
    public void clear() {
        orderRepository.deleteAll();
    }

    @Test
    public void givenHasOneClientRegistered_whenCreateNewOrderOfService_ThenReturnLocationAndBody()
            throws Exception {

        Client client = createClient("João Sousa", "joao.sousafatec@gmail.com",
                "9012-2143");

        String orderDescription = "Reparo de Mesa de Som. Volumes com mal contato";
        BigDecimal orderPrice = new BigDecimal("300.50");

        String bodyJson = "{\"client\": {\"id\": \"" + client.getId().toString()
                + "\"}, \"description\": \"" + orderDescription
                + "\", \"price\": " + orderPrice + "}";

        ResultActions result = mvc
                .perform(post(PATH).content(bodyJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.client.id", is(client.getId().toString())))
                .andExpect(jsonPath("$.client.name", is(client.getName())))
                .andExpect(jsonPath("$.client.email", is(client.getEmail())))
                .andExpect(jsonPath("$.client.phoneNumber",
                        is(client.getPhoneNumber())))
                .andExpect(jsonPath("$.description", is(orderDescription)))
                .andExpect(jsonPath("$.price", is(300.5)))
                .andExpect(jsonPath("$.status", is("OPENED")))
                .andExpect(jsonPath("$.openedDate", is(notNullValue())))
                .andExpect(jsonPath("$.finishedDate", is(nullValue())));

        OrderOfService orderCreated = orderRepository
                .findByStatus(OrderOfServiceStatuses.OPENED).get(0);

        result.andExpect(MockMvcResultMatchers.header().string("location",
                containsString("/orders-of-service/"
                        + orderCreated.getId().toString())));

    }

    @Test
    public void givenNoClientRegistered_whenCreateOrderWithUnregisteredClient_thenReturnBadRequestWithMessage()
            throws Exception {

        String randomId = UUID.randomUUID().toString();
        String orderDescription = "Reparo de Mesa de Som. Volumes com mal contato";
        BigDecimal orderPrice = new BigDecimal("300.50");

        String bodyJson = "{\"client\": {\"id\": \"" + randomId
                + "\"}, \"description\": \"" + orderDescription
                + "\", \"price\": " + orderPrice + "}";

        mvc.perform(post(PATH).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message",
                        is("O Cliente informado não foi encontrado!")));

    }

    @Test
    public void givenHasOneClientRegistered_whenCreateNewOrderWithInvalidDescription_ThenReturnBadRequestWithMessage()
            throws Exception {

        Client client = createClient("João Sousa", "joao.sousafatec@gmail.com",
                "9012-2143");

        String orderDescription = "  ";
        BigDecimal orderPrice = new BigDecimal("300.50");

        String bodyJson = "{\"client\": {\"id\": \"" + client.getId().toString()
                + "\"}, \"description\": \"" + orderDescription
                + "\", \"price\": " + orderPrice + "}";

        mvc.perform(post(PATH).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message", is(
                        "Um ou mais campos estão inválidos. Tente novamente!")))
                .andExpect(jsonPath("$.errorFields", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errorFields[0].fieldName",
                        is("description")))
                .andExpect(jsonPath("$.errorFields[0].fieldError",
                        is("must not be blank")));

    }

    @Test
    public void givenHasOneClientRegistered_whenCreateNewOrderWithInvalidClient_ThenReturnBadRequestWithMessage()
            throws Exception {

        createClient("João Sousa", "joao.sousafatec@gmail.com", "9012-2143");

        String orderDescription = "Reparo de lampada";
        BigDecimal orderPrice = new BigDecimal("300.50");

        String bodyJson = "{\"client\": {}, \"description\": \""
                + orderDescription + "\", \"price\": " + orderPrice + "}";

        mvc.perform(post(PATH).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message", is(
                        "Um ou mais campos estão inválidos. Tente novamente!")))
                .andExpect(jsonPath("$.errorFields", Matchers.hasSize(1)))
                .andExpect(
                        jsonPath("$.errorFields[0].fieldName", is("client.id")))
                .andExpect(jsonPath("$.errorFields[0].fieldError",
                        is("must not be null")));

    }

    @Test
    public void givenHasOrdersRegistered_whenFindAllOrders_thenReturnWithOk()
            throws Exception {

        Client client = createClient("João Sousa", "joao.sousafatec@gmail.com",
                "9012-2143");
        OrderOfService orderCreated = createOrderOfService(client,
                "Reparo de teclado", "24.00");

        mvc.perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(orderCreated.getId().toString())))
                .andExpect(
                        jsonPath("$[0].description", is("Reparo de teclado")))
                .andExpect(jsonPath("$[0].price", is(24.0)))
                .andExpect(jsonPath("$[0].status", is("OPENED")))
                .andExpect(jsonPath("$[0].openedDate", is(notNullValue())))
                .andExpect(jsonPath("$[0].client.id",
                        is(client.getId().toString())))
                .andExpect(jsonPath("$[0].client.name", is("João Sousa")))
                .andExpect(jsonPath("$[0].client.email",
                        is("joao.sousafatec@gmail.com")))
                .andExpect(
                        jsonPath("$[0].client.phoneNumber", is("9012-2143")));

    }

    @Test
    public void givenNoOrdersRegistered_whenFindAllOrders_thenReturnNoContent()
            throws Exception {

        mvc.perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    public void givenHasTwoOrdersRegistered_whenFindOrderById_thenReturnWithOk()
            throws Exception {

        Client client = createClient("João Sousa", "joao.sousafatec@gmail.com",
                "9012-2143");
        OrderOfService orderCreated = createOrderOfService(client,
                "Reparo de teclado", "24.00");
        createOrderOfService(client, "Reparo de Mouse", "82.00");

        mvc.perform(get(PATH + "/" + orderCreated.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id", is(orderCreated.getId().toString())))
                .andExpect(jsonPath("$.description", is("Reparo de teclado")))
                .andExpect(jsonPath("$.price", is(24.0)))
                .andExpect(jsonPath("$.status", is("OPENED")))
                .andExpect(jsonPath("$.openedDate", is(notNullValue())))
                .andExpect(
                        jsonPath("$.client.id", is(client.getId().toString())))
                .andExpect(jsonPath("$.client.name", is("João Sousa")))
                .andExpect(jsonPath("$.client.email",
                        is("joao.sousafatec@gmail.com")))
                .andExpect(jsonPath("$.client.phoneNumber", is("9012-2143")));

    }

    @Test
    public void givenHasTwoOrdersRegistered_whenFindOrderByUnregisteredId_thenReturnNotFound()
            throws Exception {

        Client client = createClient("João Sousa", "joao.sousafatec@gmail.com",
                "9012-2143");

        createOrderOfService(client, "Reparo de teclado", "24.00");
        createOrderOfService(client, "Reparo de Mouse", "82.00");

        String unregisteredId = UUID.randomUUID().toString();

        mvc.perform(get(PATH + "/" + unregisteredId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

}

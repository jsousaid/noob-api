package com.joao.noob.api.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.joao.noob.domain.repository.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientApiTest extends Utils {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientRepository repository;

    @BeforeEach
    public void clear() {
        repository.deleteAll();
    }

    @Test
    public void givenHasOneClientRegistered_whenFindClientsAllClients_thenReturnClient()
            throws Exception {

        String name = "Joao Sousa";
        String email = "joao.sousafatec@gmail.com";
        String phone = "97136720";

        Client clientCreated = createClient(name, email, phone);

        mvc.perform(get("/clients").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id",
                        is(clientCreated.getId().toString())))
                .andExpect(jsonPath("$[0].name", is(name)))
                .andExpect(jsonPath("$[0].email", is(email)))
                .andExpect(jsonPath("$[0].phoneNumber", is(phone)));

    }

    @Test
    public void givenNoClientsRegistered_whenFindClientsAllClients_thenReturnNoContent()
            throws Exception {

        mvc.perform(get("/clients").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    public void givenTwoClientsRegistered_whenFindClientByid_thenReturnCorrespondentClient()
            throws Exception {

        createClient("Fabiano da Silva", "fabiano.silva@gmail.com", "3141213");

        String clientName = "Joao Sousa";
        String clientEmail = "joao.sousafatec@gmail.com";
        String clientPhone = "97136720";

        Client clientToRetrieve = createClient(clientName, clientEmail,
                clientPhone);

        String clientid = clientToRetrieve.getId().toString();

        mvc.perform(get("/clients/" + clientid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientid)))
                .andExpect(jsonPath("$.name", is(clientName)))
                .andExpect(jsonPath("$.email", is(clientEmail)))
                .andExpect(jsonPath("$.phoneNumber", is(clientPhone)));

    }

    @Test
    public void givenOneClientRegistered_whenFindClientByUnregisteredId_thenReturnNotFound()
            throws Exception {

        createClient("Joao Sousa", "joao.sousafatec@gmail.com", "97136720");

        String unregisteredId = "ba58a092-e207-4fc1-affb-26205ce63676";

        mvc.perform(get("/clients/" + unregisteredId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void givenNoClientRegistered_whenCreateClient_thenReturnLocationAndBody()
            throws Exception {

        String clientName = "João Sousa Ma Oi";
        String clientEmail = "joao.sousa@idtrust.com.br";
        String clientPhone = "0283-2131";

        String bodyJson = "{\"name\": \"" + clientName + "\"," + "\"email\": \""
                + clientEmail + "\"," + "\"phoneNumber\": \"" + clientPhone
                + "\"}";

        ResultActions result = mvc
                .perform(post("/clients").content(bodyJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(clientName)))
                .andExpect(jsonPath("$.email", is(clientEmail)))
                .andExpect(jsonPath("$.phoneNumber", is(clientPhone)));

        Client clientCreated = repository.findByEmail(clientEmail);

        result.andExpect(MockMvcResultMatchers.header().string("location",
                containsString(
                        "/clients/" + clientCreated.getId().toString())));

        assertEquals(clientCreated.getName(), clientName);
        assertEquals(clientCreated.getEmail(), clientEmail);
        assertEquals(clientCreated.getPhoneNumber(), clientPhone);
    }

    @Test
    public void givenClientRegistered_whenCreateClientDuplicated_thenReturnBadRequestWithMessage()
            throws Exception {

        String clientName = "João Sousa Ma Oi";
        String clientEmail = "joao.sousa@idtrust.com.br";
        String clientPhone = "0283-2131";

        createClient(clientName, clientEmail, clientPhone);

        String bodyJson = "{\"name\": \"" + clientName + "\"," + "\"email\": \""
                + clientEmail + "\"," + "\"phoneNumber\": \"" + clientPhone
                + "\"}";

        mvc.perform(post("/clients").content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message",
                        is("Já existe um cliente cadastrado com este e-mail")));
    }

    // TODO: CRIAR TESTES:
    // tentando criar um client com o nome inválido
    // tentando criar um client com o email inválido
    // todos os cenários de update
    // todos os cenários de delete
}

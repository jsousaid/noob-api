package com.joao.noob.api.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void givenHasOneClientRegistered_whenFindAllClients_thenReturnClient()
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
    public void givenNoClientsRegistered_whenFindAllClients_thenReturnNoContent()
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

    @Test
    public void givenNoClientRegistered_whenCreateClientWithInvlaidEmail_thenReturnBadRequestWithMessage()
            throws Exception {

        String clientName = "João Sousa";
        String clientEmail = "joaosousafatecgmailcom";
        String clientPhone = "0283-2131";

        String bodyJson = "{\"name\": \"" + clientName + "\"," + "\"email\": \""
                + clientEmail + "\"," + "\"phoneNumber\": \"" + clientPhone
                + "\"}";

        mvc.perform(post("/clients").content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message", is(
                        "Um ou mais campos estão inválidos. Tente novamente!")))
                .andExpect(jsonPath("$.errorFields", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errorFields[0].fieldName", is("email")))
                .andExpect(jsonPath("$.errorFields[0].fieldError",
                        is("must be a well-formed email address")));

        Client clientNotCreated = repository.findByEmail(clientEmail);

        assertNull(clientNotCreated);

    }

    @Test
    public void givenNoClientRegistered_whenCreateClientWithBlankName_thenReturnBadRequestWithMessage()
            throws Exception {

        String clientName = "     ";
        String clientEmail = "joaosousafatec@gmail.com";
        String clientPhone = "0283-2131";

        String bodyJson = "{\"name\": \"" + clientName + "\"," + "\"email\": \""
                + clientEmail + "\"," + "\"phoneNumber\": \"" + clientPhone
                + "\"}";

        mvc.perform(post("/clients").content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message", is(
                        "Um ou mais campos estão inválidos. Tente novamente!")))
                .andExpect(jsonPath("$.errorFields", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errorFields[0].fieldName", is("name")))
                .andExpect(jsonPath("$.errorFields[0].fieldError",
                        is("must not be blank")));

        Client clientNotCreated = repository.findByEmail(clientEmail);

        assertNull(clientNotCreated);

    }

    @Test
    public void givenClientRegistered_whenUpdateClientName_thenReturnOkWithBody()
            throws Exception {

        String clientName = "Joao Sousa";
        String clientEmail = "joao.sousafatec@gmail.com";
        String clientPhone = "97136720";
        String newClientName = "João Paulo Sousa";

        createClient(clientName, clientEmail, clientPhone);

        Client clientBeforeUpdate = repository.findByEmail(clientEmail);
        String clientId = clientBeforeUpdate.getId().toString();

        assertEquals(clientName, clientBeforeUpdate.getName());

        String bodyJson = "{\"name\": \"" + newClientName + "\","
                + "\"email\": \"" + clientEmail + "\"," + "\"phoneNumber\": \""
                + clientPhone + "\"}";

        mvc.perform(put("/clients/" + clientId).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.name", is(newClientName)))
                .andExpect(jsonPath("$.email", is(clientEmail)))
                .andExpect(jsonPath("$.phoneNumber", is(clientPhone)));

        Client clientAfterUpdate = repository.findByEmail(clientEmail);

        assertEquals(newClientName, clientAfterUpdate.getName());

    }

    @Test
    public void givenClientRegistered_whenUpdateClientWithUnregisteredId_thenReturnNotFound()
            throws Exception {

        String clientName = "Joao Sousa";
        String clientEmail = "joao.sousafatec@gmail.com";
        String clientPhone = "97136720";
        String newClientName = "João Paulo Sousa";

        createClient(clientName, clientEmail, clientPhone);

        String invalidId = UUID.randomUUID().toString();

        String bodyJson = "{\"name\": \"" + newClientName + "\","
                + "\"email\": \"" + clientEmail + "\"," + "\"phoneNumber\": \""
                + clientPhone + "\"}";

        mvc.perform(put("/clients/" + invalidId).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());

    }

    @Test
    public void givenClientRegistered_whenUpdateClientWithInvalidNameAndEmail_thenReturnBadRequestWithMessage()
            throws Exception {

        String clientName = "Joao Sousa";
        String clientEmail = "joao.sousafatec@gmail.com";
        String clientPhone = "97136720";
        String newClientName = "   ";
        String newClientEmail = "blablabla";

        createClient(clientName, clientEmail, clientPhone);

        Client clientBeforeUpdate = repository.findByEmail(clientEmail);
        String clientId = clientBeforeUpdate.getId().toString();

        String bodyJson = "{\"name\": \"" + newClientName + "\","
                + "\"email\": \"" + newClientEmail + "\","
                + "\"phoneNumber\": \"" + clientPhone + "\"}";

        mvc.perform(put("/clients/" + clientId).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message", is(
                        "Um ou mais campos estão inválidos. Tente novamente!")))
                .andExpect(jsonPath("$.errorFields", Matchers.hasSize(2)));

    }

    @Test
    public void givenTwoClientsRegistered_whenUpdateClientWithEmailDuplicated_thenReturnBadRequestWithMessage()
            throws Exception {

        String nameClient1 = "João Sousa Ma Oi";
        String emailClient1 = "joao.sousa@idtrust.com.br";
        String phoneClient1 = "0283-2131";
        Client client1 = createClient(nameClient1, emailClient1, phoneClient1);
        String idClient1 = client1.getId().toString();

        String nameClient2 = "Maria Medeiros";
        String emailClient2 = "maria.medeiros@gmail.com.br";
        String phoneClient2 = "1324-9032";
        createClient(nameClient2, emailClient2, phoneClient2);

        String bodyJson = "{\"name\": \"" + nameClient1 + "\","
                + "\"email\": \"" + emailClient2 + "\"," + "\"phoneNumber\": \""
                + phoneClient1 + "\"}";

        mvc.perform(put("/clients/" + idClient1).content(bodyJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.time", is(notNullValue())))
                .andExpect(jsonPath("$.message",
                        is("Já existe um cliente cadastrado com este e-mail")));
    }

    @Test
    public void givenTwoClientsRegistered_whenDeletClient_thenReturnNoContent()
            throws Exception {

        String nameClient1 = "João Sousa Ma Oi";
        String emailClient1 = "joao.sousa@idtrust.com.br";
        String phoneClient1 = "0283-2131";
        Client client1 = createClient(nameClient1, emailClient1, phoneClient1);
        String idClient1 = client1.getId().toString();

        String nameClient2 = "Maria Medeiros";
        String emailClient2 = "maria.medeiros@gmail.com.br";
        String phoneClient2 = "1324-9032";
        createClient(nameClient2, emailClient2, phoneClient2);

        mvc.perform(delete("/clients/" + idClient1))
                .andExpect(status().isNoContent());

        Client clientDeleted = repository.findByEmail(emailClient1);
        Client clientNotDeleted = repository.findByEmail(emailClient2);

        assertNull(clientDeleted);
        assertNotNull(clientNotDeleted);
    }

    @Test
    public void givenClientRegistered_whenDeletClientWithUnregisteredId_thenReturnNotFound()
            throws Exception {

        String nameClient1 = "João Sousa Ma Oi";
        String emailClient1 = "joao.sousa@idtrust.com.br";
        String phoneClient1 = "0283-2131";
        createClient(nameClient1, emailClient1, phoneClient1);

        String invalidId = UUID.randomUUID().toString();

        mvc.perform(delete("/clients/" + invalidId))
                .andExpect(status().isNotFound());

        Client clientNotDeleted = repository.findByEmail(emailClient1);
        assertNotNull(clientNotDeleted);
    }
}

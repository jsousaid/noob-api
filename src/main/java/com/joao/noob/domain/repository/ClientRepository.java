package com.joao.noob.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joao.noob.domain.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findByName(String name);

    Client findByEmail(String email);

}

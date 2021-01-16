package com.joao.noob.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joao.noob.domain.model.OrderOfService;
import com.joao.noob.domain.model.OrderOfServiceStatuses;

@Repository
public interface OrderOfServiceRepository
        extends JpaRepository<OrderOfService, UUID> {

    List<OrderOfService> findByStatus(OrderOfServiceStatuses status);

}

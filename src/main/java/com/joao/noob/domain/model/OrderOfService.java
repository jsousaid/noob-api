package com.joao.noob.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.joao.noob.domain.ValidationGroups;

@Entity
public class OrderOfService {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.ClientId.class)
    @NotNull
    @ManyToOne // muitas ordem de servico possuem apenas um cliente
    private Client client;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @JsonProperty(access = Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private OrderOfServiceStatuses status;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime openedDate;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime finishedDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderOfServiceStatuses getStatus() {
        return status;
    }

    public void setStatus(OrderOfServiceStatuses status) {
        this.status = status;
    }

    public OffsetDateTime getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(OffsetDateTime openedDate) {
        this.openedDate = openedDate;
    }

    public OffsetDateTime getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(OffsetDateTime finishedDate) {
        this.finishedDate = finishedDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderOfService other = (OrderOfService) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

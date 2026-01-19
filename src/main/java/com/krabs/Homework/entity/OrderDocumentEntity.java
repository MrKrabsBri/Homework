package com.krabs.Homework.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // maybe needed more params
    private int id;

    @Column(unique = true)
    private String serviceId;

    private String serviceType;
    private String customerId;
    private String subscriptionId;
    @Embedded
    private ServiceDetailsEntity serviceDetailsEntity;
    @Embedded
    private CustomerDetailsEntity customerDetailsEntity;
}

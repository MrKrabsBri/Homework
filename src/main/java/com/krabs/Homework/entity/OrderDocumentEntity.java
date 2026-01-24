package com.krabs.Homework.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @NonNull
    private String serviceType;
    @NonNull
    private String customerId;
    @NonNull
    private String subscriptionId;
    @Embedded
    @NonNull
    private ServiceDetailsEntity serviceDetailsEntity;
    @Embedded
    @NonNull
    private CustomerDetailsEntity customerDetailsEntity;
    private Boolean isVipCustomer;
    private String specialOffer;
}

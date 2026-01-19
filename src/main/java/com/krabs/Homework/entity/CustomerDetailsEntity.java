package com.krabs.Homework.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsEntity {
    private String name;
    @Embedded
    private AddressEntity addressEntity;
    private String contactNumber;
}

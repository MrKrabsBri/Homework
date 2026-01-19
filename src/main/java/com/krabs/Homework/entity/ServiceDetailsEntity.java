package com.krabs.Homework.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailsEntity {
    private String planType;
    private String dataLimit;
    private boolean roamingEnabled;
    private List<String> additionalServices;
}

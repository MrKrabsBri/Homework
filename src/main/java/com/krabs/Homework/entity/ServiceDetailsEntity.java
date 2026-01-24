package com.krabs.Homework.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.List;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailsEntity {
   // @NonNull
    private String planType;
    private String dataLimit;
    private Boolean roamingEnabled;
    private List<String> additionalServices;
}

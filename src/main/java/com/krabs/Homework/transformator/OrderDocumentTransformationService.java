package com.krabs.Homework.transformator;

import com.customercontract.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class OrderDocumentTransformationService {

    private final List<String> ID_VIP_CUSTOMERS = new ArrayList<>(List.of("123456789"));
    private final List<String> SPECIAL_OFFERS = new ArrayList<>(List.of("ExtraData"));
    private final List<String> COUNTRIES_NO_ROAMING = new ArrayList<>(List.of("Sweden"));
    private final List<String> PLAN_TYPES = new ArrayList<>(List.of("5G"));
    private final List<String> ERRORS = new ArrayList<>(List.of("InvalidContactNumber"));

    private final List<Consumer<OrderDocument>> transformations = List.of(
            this::applyVipCustomer,
            this::applySpecialOffer,
            this::applyRoamingEnabledRemoval,
            this::applyContactNumberValidation
    );

    public void transform(OrderDocument orderDocument) {
        transformations.forEach(t -> t.accept(orderDocument));
    }


    private void applyVipCustomer(OrderDocument orderDocument) {
        if (ID_VIP_CUSTOMERS.contains(orderDocument.getCustomerId())) {
            orderDocument.setVIPCustomer(true);
        }
    }

    private void applySpecialOffer(OrderDocument orderDocument) {
        if (PLAN_TYPES.contains(orderDocument.getServiceDetails().getPlanType())
                && (orderDocument.getServiceDetails().getDataLimit() == null
                || orderDocument.getServiceDetails().getDataLimit().isEmpty())) {

            orderDocument.setSpecialOffer(SPECIAL_OFFERS.get(0));
        }
    }

    private void applyRoamingEnabledRemoval(OrderDocument orderDocument) {
        if (!COUNTRIES_NO_ROAMING.contains(
                orderDocument.getCustomerDetails().getAddress().getCountry())) {
            orderDocument.getServiceDetails().setRoamingEnabled(null);
        }
    }

    public void applyContactNumberValidation(OrderDocument orderDocument){
        if (orderDocument.getCustomerDetails().getContactNumber() == null
                || !orderDocument.getCustomerDetails().getContactNumber().matches("^\\+\\d{11,15}$")) {
            orderDocument.setError(ERRORS.get(0));
        }
    }
}

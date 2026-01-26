package com.krabs.Homework.validator;

import com.customercontract.OrderDocument;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDocumentValidator {

    public final List<String> errorMessages = List.of("Mandatory field is missing : ");

    public String validateMandatoryFields(OrderDocument orderDocument) {
        if (orderDocument.getServiceId() == null || orderDocument.getServiceId().isEmpty()) {
            return errorMessages.get(0);
        }
        if (orderDocument.getServiceType() == null || orderDocument.getServiceType().isEmpty()) {
            return errorMessages.get(0);
        }
        if (orderDocument.getCustomerId() == null || orderDocument.getCustomerId().isEmpty()) {
            return errorMessages.get(0);
        }
        if (orderDocument.getSubscriptionId() == null || orderDocument.getSubscriptionId().isEmpty()) {
            return errorMessages.get(0);
        }
        if (orderDocument.getServiceDetails() == null) {
            return errorMessages.get(0);
        }
        if (orderDocument.getServiceDetails() == null &&
                orderDocument.getServiceDetails().getPlanType() == null) {
            return errorMessages.get(0);
        }
        if (orderDocument.getServiceDetails() == null &&
                orderDocument.getServiceDetails().isRoamingEnabled() == null) {
            return errorMessages.get(0);
        }
        if (orderDocument.getCustomerDetails() == null) {
            return errorMessages.get(0);
        }
        if (orderDocument.getCustomerDetails() != null
                && (orderDocument.getCustomerDetails().getName() == null
                || orderDocument.getCustomerDetails().getName().isEmpty())) {
            return errorMessages.get(0);
        }
        if (orderDocument.getCustomerDetails() != null
                && (orderDocument.getCustomerDetails().getContactNumber() == null
                || orderDocument.getCustomerDetails().getContactNumber().isEmpty())) {
            return errorMessages.get(0);
        }
        return null;
    }
}

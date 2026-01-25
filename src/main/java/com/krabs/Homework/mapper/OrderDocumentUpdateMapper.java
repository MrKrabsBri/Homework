package com.krabs.Homework.mapper;

import com.krabs.Homework.entity.AddressEntity;
import com.krabs.Homework.entity.CustomerDetailsEntity;
import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.entity.ServiceDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class OrderDocumentUpdateMapper {

    public void updateUserEntity(OrderDocumentEntity newOrderDocument,
                                 OrderDocumentEntity orderDocumentEntityFromDB) {

        //is request/orderDocument padaryti entity,
        // paimti to entity values
        // primapinti jas prie orderDocumentEntity/
        //TODO: NULL CHECKS!!!
        //TODO: TESTS
        orderDocumentEntityFromDB.setServiceType(newOrderDocument.getServiceType());
        orderDocumentEntityFromDB.setCustomerId(newOrderDocument.getCustomerId());
        orderDocumentEntityFromDB.setSubscriptionId(newOrderDocument.getSubscriptionId());

        ServiceDetailsEntity updatedServiceDetails = new ServiceDetailsEntity();
        updatedServiceDetails.setPlanType(newOrderDocument.getServiceDetailsEntity().getPlanType());
        updatedServiceDetails.setDataLimit(newOrderDocument.getServiceDetailsEntity().getDataLimit());
        updatedServiceDetails.setRoamingEnabled(newOrderDocument.getServiceDetailsEntity().getRoamingEnabled());

        ServiceDetailsEntity existingServiceDetails = orderDocumentEntityFromDB.getServiceDetailsEntity();
        ServiceDetailsEntity newServiceDetails = newOrderDocument.getServiceDetailsEntity();
        // Update scalar fields
        existingServiceDetails.setPlanType(newServiceDetails.getPlanType());
        existingServiceDetails.setDataLimit(newServiceDetails.getDataLimit());
        existingServiceDetails.setRoamingEnabled(newServiceDetails.getRoamingEnabled());

        // Update collections safely
        if (newServiceDetails.getAdditionalServices() != null) {
            if (existingServiceDetails.getAdditionalServices() == null) {
                existingServiceDetails.setAdditionalServices(new ArrayList<>());
            }
            existingServiceDetails.getAdditionalServices().clear(); // if you want to replace old list
            existingServiceDetails.getAdditionalServices().addAll(newServiceDetails.getAdditionalServices());
        }

        CustomerDetailsEntity updatedCustomerDetails = new CustomerDetailsEntity();
        AddressEntity updatedAddress = new AddressEntity();
        updatedAddress.setStreet(newOrderDocument.getCustomerDetailsEntity().getAddressEntity().getStreet());
        updatedAddress.setCity(newOrderDocument.getCustomerDetailsEntity().getAddressEntity().getCity());
        updatedAddress.setPostalCode(newOrderDocument.getCustomerDetailsEntity().getAddressEntity().getPostalCode());
        updatedAddress.setCountry(newOrderDocument.getCustomerDetailsEntity().getAddressEntity().getCountry());
        updatedCustomerDetails.setAddressEntity(updatedAddress);
        updatedCustomerDetails.setName(newOrderDocument.getCustomerDetailsEntity().getName());
        updatedCustomerDetails.setContactNumber(newOrderDocument.getCustomerDetailsEntity().getContactNumber());
        orderDocumentEntityFromDB.setCustomerDetailsEntity(updatedCustomerDetails);

    }
}

package com.krabs.Homework.mapper;

import com.customercontract.Address;
import com.customercontract.CustomerDetails;
import com.customercontract.OrderDocument;
import com.customercontract.ServiceDetails;
import com.krabs.Homework.entity.AddressEntity;
import com.krabs.Homework.entity.CustomerDetailsEntity;
import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.entity.ServiceDetailsEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderDocumentMapper {

    public OrderDocumentEntity mapOrderDocumentSoapToEntity(OrderDocument soapOrderDocument) {
        return OrderDocumentEntity.builder()
                .serviceId(soapOrderDocument.getServiceId())
                .serviceType(soapOrderDocument.getServiceType())
                .customerId(soapOrderDocument.getCustomerId())
                .subscriptionId(soapOrderDocument.getSubscriptionId())
                .serviceDetailsEntity(mapServiceDetailsSoapToEntity(soapOrderDocument))
                .customerDetailsEntity(mapCustomerDetailsSoapToEntity(soapOrderDocument))
                .build();
    }

    public OrderDocument mapOrderDocumentEntityToSoap(OrderDocumentEntity entity) {
        OrderDocument soapOrderDocument = new com.customercontract.OrderDocument();
        soapOrderDocument.setServiceId(entity.getServiceId());
        soapOrderDocument.setServiceType(entity.getServiceType());
        soapOrderDocument.setCustomerId(entity.getCustomerId());
        soapOrderDocument.setSubscriptionId(entity.getSubscriptionId());
        soapOrderDocument.setServiceDetails(mapServiceDetailsEntityToSoap(entity.getServiceDetailsEntity()));
        soapOrderDocument.setCustomerDetails(mapCustomerDetailsEntityToSoap(entity.getCustomerDetailsEntity()));
        return soapOrderDocument;
    }

    private ServiceDetailsEntity mapServiceDetailsSoapToEntity(OrderDocument soapOrderDocument){
        return ServiceDetailsEntity.builder()
                .planType(soapOrderDocument.getServiceDetails().getPlanType())
                .dataLimit(soapOrderDocument.getServiceDetails().getDataLimit())
                .roamingEnabled(soapOrderDocument.getServiceDetails().isRoamingEnabled())
                .additionalServices(soapOrderDocument.getServiceDetails().getAdditionalServices())
                .build();
    }

    private CustomerDetailsEntity mapCustomerDetailsSoapToEntity(OrderDocument soapOrderDocument){
        return CustomerDetailsEntity.builder()
                .name(soapOrderDocument.getCustomerDetails().getName())
                .addressEntity(mapAddressSoapToEntity(soapOrderDocument))
                .contactNumber(soapOrderDocument.getCustomerDetails().getContactNumber())
                .build();
    }

    private AddressEntity mapAddressSoapToEntity(OrderDocument soapOrderDocument){
        return AddressEntity.builder()
                .street(soapOrderDocument.getCustomerDetails().getAddress().getStreet())
                .city(soapOrderDocument.getCustomerDetails().getAddress().getCity())
                .postalCode(soapOrderDocument.getCustomerDetails().getAddress().getPostalCode())
                .country(soapOrderDocument.getCustomerDetails().getAddress().getCountry())
                .build();
    }

    private ServiceDetails mapServiceDetailsEntityToSoap(ServiceDetailsEntity entity) {
        ServiceDetails soap = new ServiceDetails();
        soap.setPlanType(entity.getPlanType());
        soap.setDataLimit(entity.getDataLimit());
        soap.setRoamingEnabled(entity.isRoamingEnabled());
        soap.getAdditionalServices().addAll(entity.getAdditionalServices());
        return soap;
    }

    private Address mapAddressEntityToSoap(AddressEntity entity) {
        Address soap = new Address();
        soap.setStreet(entity.getStreet());
        soap.setCity(entity.getCity());
        soap.setPostalCode(entity.getPostalCode());
        soap.setCountry(entity.getCountry());
        return soap;
    }

    private CustomerDetails mapCustomerDetailsEntityToSoap(CustomerDetailsEntity entity) {
        CustomerDetails soap = new CustomerDetails();
        soap.setName(entity.getName());
        soap.setContactNumber(entity.getContactNumber());
        soap.setAddress(mapAddressEntityToSoap(entity.getAddressEntity()));
        return soap;
    }
}

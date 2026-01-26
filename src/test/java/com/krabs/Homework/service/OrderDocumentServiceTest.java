package com.krabs.Homework.service;

import com.customercontract.*;
import com.krabs.Homework.entity.AddressEntity;
import com.krabs.Homework.entity.CustomerDetailsEntity;
import com.krabs.Homework.entity.OrderDocumentEntity;
import com.krabs.Homework.entity.ServiceDetailsEntity;
import com.krabs.Homework.exception.DuplicateServiceIdException;
import com.krabs.Homework.exception.OrderDocumentNotFoundException;
import com.krabs.Homework.mapper.OrderDocumentMapper;
import com.krabs.Homework.mapper.OrderDocumentUpdateMapper;
import com.krabs.Homework.repository.CustomerContractRepository;
import com.krabs.Homework.transformator.OrderDocumentTransformationService;
import com.krabs.Homework.validator.OrderDocumentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class OrderDocumentServiceTest {

    @Mock
    CustomerContractRepository repository;
    @Mock
    OrderDocumentMapper orderDocumentMapper;
    @Spy
    OrderDocumentUpdateMapper orderDocumentUpdateMapper;
    @Mock
    private OrderDocumentTransformationService orderDocumentTransformationService;
    @Mock
    private OrderDocumentValidator orderDocumentValidator;
    @InjectMocks
    OrderDocumentService service;

    private OrderDocument fullyValidOrderDocument() {
        OrderDocument doc = new OrderDocument();
        doc.setServiceId("SVC-123");
        doc.setServiceType("MOBILE");
        doc.setCustomerId("999999999");
        doc.setSubscriptionId("SUB-001");

        ServiceDetails sd = new ServiceDetails();
        sd.setPlanType("5G");
        sd.setDataLimit("10GB");
        sd.setRoamingEnabled(true);
        sd.getAdditionalServices().add("SMS");
        doc.setServiceDetails(sd);

        Address address = new Address();
        address.setStreet("Main St");
        address.setCity("Stockholm");
        address.setPostalCode("12345");
        address.setCountry("Sweden");

        CustomerDetails cd = new CustomerDetails();
        cd.setName("John Doe");
        cd.setContactNumber("+37061234567");
        cd.setAddress(address);

        doc.setCustomerDetails(cd);

        return doc;
    }

    private OrderDocumentEntity fullyPopulatedEntity() {
        ServiceDetailsEntity sd = new ServiceDetailsEntity();
        sd.setPlanType("5G");
        sd.setDataLimit("10GB");
        sd.setRoamingEnabled(true);
        sd.setAdditionalServices(new ArrayList<>(List.of("SMS")));

        AddressEntity addr = new AddressEntity();
        addr.setStreet("Main");
        addr.setCity("Stockholm");
        addr.setPostalCode("12345");
        addr.setCountry("Sweden");

        CustomerDetailsEntity cd = new CustomerDetailsEntity();
        cd.setName("John Doe");
        cd.setContactNumber("+37061234567");
        cd.setAddressEntity(addr);

        return OrderDocumentEntity.builder()
                .serviceId("SVC-123")
                .serviceType("MOBILE")
                .customerId("999999999")
                .subscriptionId("SUB-001")
                .serviceDetailsEntity(sd)
                .customerDetailsEntity(cd)
                .isVipCustomer(false)
                .specialOffer(null)
                .build();
    }

    private OrderDocument createOrderDocumentWithContact(String contactNumber) {
        OrderDocument order = fullyValidOrderDocument();
        order.getCustomerDetails().setContactNumber(contactNumber);
        return order;
    }

    @Test
    void shouldCreateOrderDocument() {
        OrderDocument orderDocument = new OrderDocument();
        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(orderDocument);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(orderDocument))
                .thenReturn(entity);

        service.createOrderDocument(request);

        verify(repository).save(entity);
        verify(repository).flush();
    }

    @Test
    void shouldPersistOrderDocument() {
        OrderDocument requestDoc = new OrderDocument();
        requestDoc.setServiceId("SVC-1");

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(requestDoc);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("SVC-1");

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(requestDoc))
                .thenReturn(entity);

        service.createOrderDocument(request);

        verify(orderDocumentMapper).mapOrderDocumentSoapToEntity(requestDoc);
        verify(repository, times(1)).save(entity);
        verify(repository, times(1)).flush();
    }

    @Test
    void createOrderDocument_shouldSaveEntity() {
        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("001");

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(soapDoc);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("001");

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(soapDoc))
                .thenReturn(entity);

        service.createOrderDocument(request);

        verify(orderDocumentMapper).mapOrderDocumentSoapToEntity(soapDoc);
        verify(repository).save(entity);
        verify(repository).flush();
    }

    @Test
    void createOrderDocument_shouldReturnSuccessResponse() {
        OrderDocument validSoap = fullyValidOrderDocument();

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(validSoap);

        OrderDocumentEntity mappedEntity = new OrderDocumentEntity();
        mappedEntity.setServiceId("SVC-123");

        when(orderDocumentValidator.validateMandatoryFields(validSoap))
                .thenReturn(null);

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(validSoap))
                .thenReturn(mappedEntity);

        CreateOrderDocumentResponse response =
                service.createOrderDocument(request);

        assertNotNull(response);
        assertNotNull(response.getResponse());
        assertEquals("Success", response.getResponse().getStatus());
        assertEquals(
                "Service activated successfully",
                response.getResponse().getMessage()
        );

        verify(orderDocumentTransformationService)
                .transform(validSoap);
        verify(repository).save(mappedEntity);
        verify(repository).flush();
    }

    @Test
    void createOrderDocument_shouldThrowException_whenDuplicateServiceId() {
        OrderDocument validSoap = fullyValidOrderDocument();

        CreateOrderDocumentRequest request = new CreateOrderDocumentRequest();
        request.setOrderDocument(validSoap);

        OrderDocumentEntity mappedEntity = new OrderDocumentEntity();
        mappedEntity.setServiceId("SVC-123");

        when(orderDocumentValidator.validateMandatoryFields(validSoap))
                .thenReturn(null);

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(validSoap))
                .thenReturn(mappedEntity);

        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(repository)
                .save(mappedEntity);

        DuplicateServiceIdException exception =
                assertThrows(
                        DuplicateServiceIdException.class,
                        () -> service.createOrderDocument(request)
                );

        assertTrue(
                exception.getMessage().contains("Service with ID SVC-123")
        );

        verify(orderDocumentTransformationService)
                .transform(validSoap);
        verify(repository).save(mappedEntity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument() {
        String serviceId = "SERVICE_123";

        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId(serviceId);

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId(serviceId);

        OrderDocument soapDocument = new OrderDocument();

        when(repository.findByServiceId(serviceId))
                .thenReturn(Optional.of(entity));
        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDocument);

        GetOrderDocumentByIdResponse response =
                service.getOrderDocumentById(request);

        assertNotNull(response);
        assertNotNull(response.getOrderDocument());
        assertEquals(soapDocument, response.getOrderDocument());

        verify(repository).findByServiceId(serviceId);
        verify(orderDocumentMapper)
                .mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andVIP() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("123456789");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("123456789");
        if ("123456789".equals(entity.getServiceId())){
            entity.setIsVipCustomer(true);
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("123456789");
        if (soapDoc.getServiceId().equals("123456789")){
            soapDoc.setVIPCustomer(true);
        }

        when(repository.findByServiceId("123456789"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        GetOrderDocumentByIdResponse response = service.getOrderDocumentById(request);

        assertNotNull(response);
        System.out.println(response.getOrderDocument().getCustomerId());
        System.out.println(response.getOrderDocument().isVIPCustomer());
        assertEquals("123456789", response.getOrderDocument().getServiceId());
        assertEquals(true, response.getOrderDocument().isVIPCustomer());

        verify(repository).findByServiceId("123456789");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andNotVIP() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("1111111111");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("1111111111");
        if ("123456789".equals(entity.getServiceId())){
            entity.setIsVipCustomer(true);
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("1111111111");
        if (soapDoc.getServiceId().equals("123456789")){
            soapDoc.setVIPCustomer(true);
        }

        when(repository.findByServiceId("1111111111"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        GetOrderDocumentByIdResponse response = service.getOrderDocumentById(request);

        assertNotNull(response);
        assertNull(response.getOrderDocument().isVIPCustomer());

        verify(repository).findByServiceId("1111111111");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentById_shouldReturnOrderDocument_andSpecialOffer() {
        GetOrderDocumentByIdRequest request = new GetOrderDocumentByIdRequest();
        request.setServiceId("150");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("150");
        ServiceDetailsEntity serviceDetailsEntity = new ServiceDetailsEntity();
        serviceDetailsEntity.setPlanType("5G");
        entity.setServiceDetailsEntity(serviceDetailsEntity);
        if ("5G".equals(entity.getServiceDetailsEntity().getPlanType())
        && (entity.getServiceDetailsEntity().getDataLimit() == null)){
            entity.setSpecialOffer("ExtraData");
        }

        OrderDocument soapDoc = new OrderDocument();
        soapDoc.setServiceId("150");
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setPlanType("5G");
        soapDoc.setServiceDetails(serviceDetails);
        if ("5G".equals(soapDoc.getServiceDetails().getPlanType()) &&
        soapDoc.getServiceDetails().getDataLimit() == null){
            soapDoc.setSpecialOffer("ExtraData");
        }

        when(repository.findByServiceId("150"))
                .thenReturn(Optional.of(entity));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity))
                .thenReturn(soapDoc);

        GetOrderDocumentByIdResponse response = service.getOrderDocumentById(request);

        assertNotNull(response);
        assertEquals("ExtraData", response.getOrderDocument().getSpecialOffer());

        verify(repository).findByServiceId("150");
        verify(orderDocumentMapper).mapOrderDocumentEntityToSoap(entity);
    }

    @Test
    void getOrderDocumentList_shouldReturnAllMappedOrderDocuments() {
        OrderDocumentEntity entity1 = new OrderDocumentEntity();
        entity1.setServiceId("SVC-1");

        OrderDocumentEntity entity2 = new OrderDocumentEntity();
        entity2.setServiceId("SVC-2");

        OrderDocument soap1 = new OrderDocument();
        soap1.setServiceId("SVC-1");

        OrderDocument soap2 = new OrderDocument();
        soap2.setServiceId("SVC-2");

        when(repository.findAll())
                .thenReturn(List.of(entity1, entity2));

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity1))
                .thenReturn(soap1);

        when(orderDocumentMapper.mapOrderDocumentEntityToSoap(entity2))
                .thenReturn(soap2);

        GetOrderDocumentListResponse response =
                service.getOrderDocumentList();

        assertNotNull(response);
        assertNotNull(response.getOrderDocument());
        assertEquals(2, response.getOrderDocument().size());

        assertEquals("SVC-1",
                response.getOrderDocument().get(0).getServiceId());
        assertEquals("SVC-2",
                response.getOrderDocument().get(1).getServiceId());

        verify(repository).findAll();
        verify(orderDocumentMapper, times(2))
                .mapOrderDocumentEntityToSoap(any(OrderDocumentEntity.class));
    }

    @Test
    void updateOrderDocumentById_shouldUpdateAndReturnUpdatedDocument() {
        String serviceId = "SVC-123";

        OrderDocumentEntity existingEntity = fullyPopulatedEntity();
        existingEntity.setServiceId(serviceId);
        existingEntity.setServiceType("OLD_TYPE");

        when(repository.findByServiceId(serviceId))
                .thenReturn(Optional.of(existingEntity));

        OrderDocument updatedSoap = fullyValidOrderDocument();
        updatedSoap.setServiceId(serviceId);
        updatedSoap.setServiceType("UPDATED_TYPE");

        UpdateOrderDocumentByIdRequest request =
                new UpdateOrderDocumentByIdRequest();
        request.setOrderDocument(updatedSoap);

        OrderDocumentEntity mappedNewEntity = fullyPopulatedEntity();
        mappedNewEntity.setServiceType("UPDATED_TYPE");

        when(orderDocumentValidator.validateMandatoryFields(updatedSoap))
                .thenReturn(null);

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(updatedSoap))
                .thenReturn(mappedNewEntity);

        UpdateOrderDocumentByIdResponse response =
                service.updateOrderDocumentById(request);

        assertNotNull(response);
        assertNotNull(response.getResponse());
        assertEquals("Success", response.getResponse().getStatus());
        assertEquals(
                "Service updated successfully",
                response.getResponse().getMessage()
        );

        verify(orderDocumentTransformationService)
                .transform(updatedSoap);
        verify(orderDocumentUpdateMapper)
                .updateUserEntity(mappedNewEntity, existingEntity);
        verify(repository)
                .save(existingEntity);

        assertEquals(
                "UPDATED_TYPE",
                existingEntity.getServiceType()
        );
    }

    @Test
    void updateOrderDocumentById_shouldThrowException_whenNotFound() {
        OrderDocument requestSoap = new OrderDocument();
        requestSoap.setServiceId("MISSING");

        UpdateOrderDocumentByIdRequest request =
                new UpdateOrderDocumentByIdRequest();
        request.setOrderDocument(requestSoap);

        when(repository.findByServiceId("MISSING"))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderDocumentNotFoundException.class,
                () -> service.updateOrderDocumentById(request)
        );

        verify(repository).findByServiceId("MISSING");
        verifyNoInteractions(orderDocumentMapper);
        verifyNoInteractions(orderDocumentUpdateMapper);
    }

    @Test
    void deleteOrderDocumentById_shouldDeleteEntityAndReturnResponse() {
        DeleteOrderDocumentByIdRequest request =
                new DeleteOrderDocumentByIdRequest();
        request.setServiceId("SVC-999");

        OrderDocumentEntity entity = new OrderDocumentEntity();
        entity.setServiceId("SVC-999");

        when(repository.findByServiceId("SVC-999"))
                .thenReturn(Optional.of(entity));

        DeleteOrderDocumentByIdResponse response =
                service.deleteOrderDocumentById(request);

        assertNotNull(response);
        assertEquals(
                "Entry with id SVC-999 was deleted successfully",
                response.getOrderDeletedMessage()
        );

        verify(repository).findByServiceId("SVC-999");
        verify(repository).delete(entity);
    }

    @Test
    void deleteOrderDocumentById_shouldThrowException_whenNotFound() {
        DeleteOrderDocumentByIdRequest request =
                new DeleteOrderDocumentByIdRequest();
        request.setServiceId("MISSING");

        when(repository.findByServiceId("MISSING"))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderDocumentNotFoundException.class,
                () -> service.deleteOrderDocumentById(request)
        );

        verify(repository).findByServiceId("MISSING");
        verify(repository, never()).delete(any());
    }

    @Test
    void updateOrderDocumentById_shouldResetVipCustomerIfCustomerIdIsNotVip() {
        String serviceId = "SVC-123";

        OrderDocumentEntity existingEntity = fullyPopulatedEntity();
        existingEntity.setServiceId(serviceId);
        existingEntity.setCustomerId("123456789");
        existingEntity.setIsVipCustomer(true);

        when(repository.findByServiceId(serviceId))
                .thenReturn(Optional.of(existingEntity));

        OrderDocument updatedSoap = fullyValidOrderDocument();
        updatedSoap.setServiceId(serviceId);
        updatedSoap.setCustomerId("999999999");
        updatedSoap.setServiceType("UPDATED_TYPE");

        UpdateOrderDocumentByIdRequest request =
                new UpdateOrderDocumentByIdRequest();
        request.setOrderDocument(updatedSoap);

        OrderDocumentEntity mappedNewEntity = fullyPopulatedEntity();
        mappedNewEntity.setCustomerId("999999999");
        mappedNewEntity.setIsVipCustomer(false);

        when(orderDocumentValidator.validateMandatoryFields(updatedSoap))
                .thenReturn(null);

        when(orderDocumentMapper.mapOrderDocumentSoapToEntity(updatedSoap))
                .thenReturn(mappedNewEntity);

        UpdateOrderDocumentByIdResponse response =
                service.updateOrderDocumentById(request);

        assertNotNull(response);
        assertNotNull(response.getResponse());
        assertEquals("Success", response.getResponse().getStatus());

        verify(orderDocumentTransformationService)
                .transform(updatedSoap);
        verify(orderDocumentUpdateMapper)
                .updateUserEntity(mappedNewEntity, existingEntity);
        verify(repository)
                .save(existingEntity);

        assertFalse(existingEntity.getIsVipCustomer());
    }

    @Test
    void createOrderDocument_shouldNotSetErrorForValidContactNumber() {
        OrderDocument validOrder = createOrderDocumentWithContact("+370612345678");

        orderDocumentTransformationService.transform(validOrder);

        assertNull(validOrder.getError(), "Valid contact number should not set error");
    }
}
